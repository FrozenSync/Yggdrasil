package com.github.frozensync.music

import com.github.frozensync.discord.command.Command
import com.github.frozensync.discord.command.CommandArgs
import com.github.frozensync.discord.command.CommandSet
import kotlinx.coroutines.reactive.awaitFirstOrNull

class MusicCommandSet : CommandSet {

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["join"] = h@{ event ->
            val member = event.member.orElse(null) ?: return@h
            val voiceState = member.voiceState.awaitFirstOrNull() ?: return@h
            val channel = voiceState.channel.awaitFirstOrNull() ?: return@h

            val provider = GuildAudioManager.of(channel.guildId).provider

            channel.join { spec -> spec.setProvider(provider) }.block()
        }

        this["play"] = h@{ event ->
            val guildId = event.guildId.orElse(null) ?: return@h
            val songUri = event.message.content.let { CommandArgs(it).split(" ")[0] }

            val scheduler = GuildAudioManager.of(guildId).scheduler

            PLAYER_MANAGER.loadItem(songUri, scheduler)
        }
    }
}
