package com.github.frozensync

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.frozensync.discord.cli.AbstractDiscordCommand
import kotlin.random.Random

class Miscellaneous : AbstractDiscordCommand(name = "misc") {
    override fun run() = Unit
}

class PickCommand : AbstractDiscordCommand() {

    val optionsString by argument()

    override fun run() {
        optionsString
            .split("|")
            .random()
            .run(::echo)
    }
}

class FlipCoinCommand : AbstractDiscordCommand() {
    override fun run() {
        Random.nextInt(2)
            .let { if (it == 0) "HEAD" else "TAILS" }
            .run(::echo)
    }
}
