package com.github.frozensync.music

import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.frozensync.discord.cli.AbstractDiscordCommand
import discord4j.core.`object`.VoiceState
import discord4j.core.event.domain.VoiceStateUpdateEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import mu.KotlinLogging
import reactor.core.publisher.Mono
import java.time.Duration

class Music : AbstractDiscordCommand() {
    override fun run() = Unit
}

class JoinCommand : AbstractDiscordCommand() {

    private val logger = KotlinLogging.logger { }

    private val event by requireObject<MessageCreateEvent>()

    override fun run() {
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

class PlayCommand : AbstractDiscordCommand() {

    private val event by requireObject<MessageCreateEvent>()

    private val songUri by argument(name = "song")

    override fun run() {
        val guildId = event.guildId.orElse(null) ?: return
        val scheduler = GuildAudioManager.of(guildId).scheduler

        PLAYER_MANAGER.loadItem(songUri, scheduler)
    }
}