package com.github.frozensync.discord.cli

import com.github.ajalt.clikt.core.subcommands
import com.github.frozensync.*

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
                ),
            Miscellaneous()
                .subcommands(
                    PickCommand(),
                    FlipCoinCommand(),
                ),
        )
