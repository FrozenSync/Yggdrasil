package com.github.frozensync

import com.github.frozensync.command.CommandRegistry
import com.github.frozensync.monitoring.MonitoringCommandSet
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main(args: Array<String>) = runBlocking<Unit> {
    if (args.isEmpty()) {
        System.err.println("Please supply a Discord bot token in args.")
        exitProcess(1)
    }
    val token = args[0]
    val client = DiscordClientBuilder(token).build()

    val commandRepository = CommandRegistry
        .register(MonitoringCommandSet)

    client.eventDispatcher.on(MessageCreateEvent::class.java).asFlow()
        .filter { event -> event.message.author.map { !it.isBot }.orElse(false) }
        .onEach { event ->
            val command = event.message.content
                .map { it.parseCommandName() }
                .map { commandRepository.findByName(it) }
                .orElse(null)

            command?.invoke(event)
        }
        .launchIn(this)

    client.login().awaitFirstOrNull()
}

private fun String.parseCommandName() = this.substringBefore(' ').removePrefix("!")
