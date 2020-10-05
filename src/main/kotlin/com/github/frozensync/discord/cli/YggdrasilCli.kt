package com.github.frozensync.discord.cli

import com.github.ajalt.clikt.core.subcommands
import com.github.frozensync.*
import com.github.frozensync.music.JoinCommand
import com.github.frozensync.music.Music
import com.github.frozensync.music.PlayCommand
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

internal val yggdrasilCli : () -> Yggdrasil = {
    Yggdrasil()
        .subcommands(
            Music()
                .subcommands(
                    JoinCommand(),
                    PlayCommand(),
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
