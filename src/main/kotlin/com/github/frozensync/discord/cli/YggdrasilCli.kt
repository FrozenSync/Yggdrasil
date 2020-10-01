package com.github.frozensync.discord.cli

import com.github.ajalt.clikt.core.subcommands
import com.github.frozensync.HealthCheck
import com.github.frozensync.PingCommand
import com.github.frozensync.UptimeCommand

class Yggdrasil : AbstractDiscordCommand(name = "@Yggdrasil") {
    override fun run() = Unit
}

internal val yggdrasilCli =
    Yggdrasil()
        .subcommands(
            HealthCheck()
                .subcommands(
                    PingCommand(),
                    UptimeCommand(),
                )
        )
