package com.github.frozensync.discord.command

import com.github.frozensync.discord.cli.yggdrasilCli
import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.awaitFirst
import mu.KotlinLogging
import org.koin.core.KoinComponent

object CommandHandler : KoinComponent {

    private val logger = KotlinLogging.logger { }

    /**
     * Listens to incoming [MessageCreateEvent]s and execute the containing command.
     */
    suspend fun executeCommands(flow: Flow<MessageCreateEvent>) = flow
        .filter { it.message.hasHumanAuthor() }
        .filter { it.message.containsPrefix("""<@!${it.client.selfId.asString()}>""") }
        .collect { executeCommand(it) }

    private suspend fun executeCommand(event: MessageCreateEvent) {
        logger.entry(event)

        val channel = event.message.channel.awaitFirst()
        val args = event.message.content.split(" ").drop(1).also { logger.debug { "Args: $it" } }

        yggdrasilCli.execute(channel, args)

        logger.exit()
    }

    private fun Message.hasHumanAuthor(): Boolean = author.map { !it.isBot }.orElse(false)

    private fun Message.containsPrefix(prefix: String) = content.startsWith(prefix)
}
