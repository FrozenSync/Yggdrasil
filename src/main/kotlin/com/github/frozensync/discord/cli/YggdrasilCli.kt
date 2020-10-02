package com.github.frozensync.discord.cli

import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.frozensync.*
import com.github.frozensync.music.JoinCommand
import com.github.frozensync.music.Music
import com.github.frozensync.music.PlayCommand
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitFirst

class Root(private val event: MessageCreateEvent) : AbstractDiscordCommand(name = "@Yggdrasil") {
    override fun run() {
        currentContext.obj = event
    }
}

object YggdrasilCli {

    suspend fun withContext(event: MessageCreateEvent): Root {
        val channel = event.message.channel.awaitFirst()

        return Root(event)
            .context {
                console = DiscordConsole(channel)
            }
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
}
