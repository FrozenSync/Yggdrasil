package com.github.frozensync.music

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.frozensync.command.cli.AbstractCommand
import com.github.frozensync.command.cli.AbstractCommandCategory
import discord4j.core.`object`.VoiceState
import discord4j.core.event.domain.VoiceStateUpdateEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import mu.KotlinLogging
import reactor.core.publisher.Mono
import java.time.Duration

class Music : AbstractCommandCategory()

class JoinVoiceChannelCommand : AbstractCommand(name = "join") {

    private val logger = KotlinLogging.logger { }

    override suspend fun execute(event: MessageCreateEvent) {
        logger.entry()

        val member = event.member.orElse(null) ?: return

        member.voiceState
            .flatMap { it.channel }
            .flatMap { channel ->
                channel
                    .join { spec ->
                        val provider = GuildAudioManager.of(channel.guildId).provider
                        spec.setProvider(provider)
                    }
                    .flatMap { connection ->
                        val isAlone = channel.voiceStates
                            .count()
                            .map { count -> 1L == count }

                        val onDelay = Mono.delay(Duration.ofSeconds(10L))
                            .filterWhen { isAlone }
                            .switchIfEmpty(Mono.never())
                            .then()

                        val onEvent = channel.client.eventDispatcher.on(VoiceStateUpdateEvent::class.java)
                            // filter is not strictly necessary, but it does prevent many unnecessary cache calls
                            .filter { event ->
                                event.old.flatMap(VoiceState::getChannelId).map(channel.id::equals).orElse(false)
                            }
                            .filterWhen { isAlone }
                            .next()
                            .then()

                        Mono.first(onDelay, onEvent).then(connection.disconnect())
                    }
            }
            .subscribe { logger.trace { "Sub $it" } }

        logger.exit()
    }
}

class PlayMusicCommand : AbstractCommand(name = "play") {

    private val songUri by argument(name = "song")

    override suspend fun execute(event: MessageCreateEvent) {
        val guildId = event.guildId.orElse(null) ?: return
        val scheduler = GuildAudioManager.of(guildId).scheduler

        PLAYER_MANAGER.loadItem(songUri, scheduler)
    }
}
