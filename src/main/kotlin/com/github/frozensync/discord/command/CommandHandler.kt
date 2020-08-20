package com.github.frozensync.discord.command

import com.github.frozensync.games.wordsnake.WordSnakeCommandSet
import com.github.frozensync.HealthCheckCommandSet
import com.github.frozensync.utility.FunCommandSet
import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.get

object CommandHandler : KoinComponent {

    private val logger = KotlinLogging.logger { }

    private val commandRepository = CommandRegistry
        .register(HealthCheckCommandSet())
        .register(FunCommandSet())
        .register(get<WordSnakeCommandSet>())

    /**
     * Listens to incoming [MessageCreateEvent]s and execute the containing command.
     */
    suspend fun executeCommands(flow: Flow<MessageCreateEvent>) = flow
        .filter { it.message.hasHumanAuthor() }
        .filter { it.message.containsPrefix("!") }
        .collect { executeCommand(it) }

    private suspend fun executeCommand(event: MessageCreateEvent) {
        logger.entry(event)

        val commandName = event.message.content.parseCommandName()
        val command = commandRepository.findByName(commandName)
        command?.invoke(event)

        logger.exit()
    }

    private fun Message.hasHumanAuthor(): Boolean = author.map { !it.isBot }.orElse(false)

    private fun Message.containsPrefix(prefix: String) = content.startsWith(prefix)

    private fun String.parseCommandName() = this.substringBefore(' ').removePrefix("!")
}
