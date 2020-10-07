package com.github.frozensync.command.cli

import com.github.ajalt.clikt.core.subcommands
import com.github.frozensync.*
import com.github.frozensync.games.shiritori.*
import com.github.frozensync.music.JoinVoiceChannelCommand
import com.github.frozensync.music.Music
import com.github.frozensync.music.PlayMusicCommand
import discord4j.core.event.domain.message.MessageCreateEvent

internal class Yggdrasil : AbstractCommand(name = "@Yggdrasil") {
    override suspend fun execute(event: MessageCreateEvent) {
        @Suppress("UNCHECKED_CAST") val parsedCommands = currentContext.obj as List<AbstractCommand>
        parsedCommands.forEach { it.execute(event) }
    }

    override fun run() {
        currentContext.obj = mutableListOf<AbstractCommand>()
    }
}

internal val yggdrasilCli: () -> Yggdrasil = {
    Yggdrasil()
        .subcommands(
            ShiritoriCategory()
                .subcommands(
                    NewGameCommand(),
                    PlayWordCommand(),
                    ForfeitCommand(),
                    CurrentTurnCommand()
                ),
            Music()
                .subcommands(
                    JoinVoiceChannelCommand(),
                    PlayMusicCommand(),
                ),
            HealthCheck()
                .subcommands(
                    PingCommand(),
                    UptimeCommand(),
                ),
            Miscellaneous()
                .subcommands(
                    PickCommand(),
                    FlipCoinCommand(),
                ),
        )
}
