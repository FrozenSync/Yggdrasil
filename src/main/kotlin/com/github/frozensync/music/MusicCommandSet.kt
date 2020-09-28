package com.github.frozensync.music

import com.github.frozensync.discord.command.Command
import com.github.frozensync.discord.command.CommandArgs
import com.github.frozensync.discord.command.CommandSet
import discord4j.core.`object`.VoiceState
import discord4j.core.event.domain.VoiceStateUpdateEvent
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import java.time.Duration

class MusicCommandSet : CommandSet {

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["join"] = h@{ event ->
            val member = event.member.orElse(null) ?: return@h
            val voiceState = member.voiceState.awaitFirstOrNull() ?: return@h
            val channel = voiceState.channel.awaitFirstOrNull() ?: return@h

            val provider = GuildAudioManager.of(channel.guildId).provider

            channel
                .join { spec -> spec.setProvider(provider) }
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
                .block()
        }

        this["play"] = h@{ event ->
            val guildId = event.guildId.orElse(null) ?: return@h
            val songUri = event.message.content.let { CommandArgs(it).split(" ")[0] }

            val scheduler = GuildAudioManager.of(guildId).scheduler

            PLAYER_MANAGER.loadItem(songUri, scheduler)
        }
    }
}
