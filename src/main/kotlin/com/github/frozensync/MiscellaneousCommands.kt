package com.github.frozensync

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.frozensync.command.cli.AbstractCommand
import com.github.frozensync.command.cli.AbstractCommandCategory
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitSingle
import kotlin.random.Random

class Miscellaneous : AbstractCommandCategory(name = "misc")

class PickCommand : AbstractCommand() {

    val optionsString by argument()

    override suspend fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.awaitSingle()
        val result = optionsString.split("|").random()
        channel.createMessage(result).awaitSingle()
    }
}

class FlipCoinCommand : AbstractCommand() {
    override suspend fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.awaitSingle()
        val result = Random.nextInt(2).let { if (it == 0) "HEAD" else "TAILS" }
        channel.createMessage(result).awaitSingle()
    }
}
