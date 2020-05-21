package com.github.frozensync.utility

import com.github.frozensync.command.Command
import com.github.frozensync.command.CommandArgs
import com.github.frozensync.command.CommandSet
import kotlinx.coroutines.reactive.awaitFirst
import kotlin.random.Random

class FunCommandSet : CommandSet {

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["pick"] = h@{ event ->
            val channel = event.message.channel.awaitFirst()
            val message = event.message.content
                .map { CommandArgs(it).split("|").random() }
                .orElse(null) ?: return@h

            channel.createMessage(message).awaitFirst()
        }

        this["flipcoin"] = { event ->
            val channel = event.message.channel.awaitFirst()
            val message = Random.nextInt(2)
                .let { if (it == 0) "HEAD" else "TAILS" }

            channel.createMessage(message).awaitFirst()
        }
    }
}
