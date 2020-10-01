package com.github.frozensync.discord.cli

import com.github.ajalt.clikt.output.CliktConsole
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import mu.KotlinLogging

class DiscordConsole(private val channel: MessageChannel) : CliktConsole {

    private val logger = KotlinLogging.logger { }

    override val lineSeparator: String get() = "\n"

    override fun print(text: String, error: Boolean) {
        logger.entry(text, error)

        val onSuccess: (result: Message) -> Unit = { logger.trace { "Sent $it" } }
        val onError: (e: Throwable) -> Unit = { e -> logger.catching(e) }

        channel.createMessage(text).subscribe(onSuccess, onError)

        logger.exit()
    }

    override fun promptForLine(prompt: String, hideInput: Boolean): String? {
        TODO("Not yet implemented")
    }
}
