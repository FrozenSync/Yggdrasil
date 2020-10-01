package com.github.frozensync

import com.github.frozensync.discord.cli.AbstractDiscordCommand
import java.lang.management.ManagementFactory
import java.time.Duration

class HealthCheck : AbstractDiscordCommand(name = "health") {
    override fun run() = Unit
}

class PingCommand : AbstractDiscordCommand() {
    override fun run() {
        echo("Pong!")
    }
}

class UptimeCommand : AbstractDiscordCommand() {
    override fun run() {
        val uptime = ManagementFactory.getRuntimeMXBean().uptime
        val duration = Duration.ofMillis(uptime)
        val days = duration.toDaysPart()
        val hours = duration.toHoursPart()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()

        echo("""$days day(s), $hours hour(s), $minutes minute(s), $seconds second(s)""")
    }
}
