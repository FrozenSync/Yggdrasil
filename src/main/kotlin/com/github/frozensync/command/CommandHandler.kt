package com.github.frozensync.command

import com.github.frozensync.command.cli.yggdrasilCli
import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

object CommandHandler {

    /**
     * Listens to incoming [MessageCreateEvent]s and execute the containing command.
     */
    suspend fun executeCommands(flow: Flow<MessageCreateEvent>) = flow
        .filter { it.message.hasHumanAuthor() }
        .filter { it.message.containsPrefix("""<@!${it.client.selfId.asString()}>""") }
        .collect {
            yggdrasilCli().parse(it.message.content)?.execute(it)
        }

    private fun Message.hasHumanAuthor(): Boolean = author.map { !it.isBot }.orElse(false)

    private fun Message.containsPrefix(prefix: String) = content.startsWith(prefix)
}
