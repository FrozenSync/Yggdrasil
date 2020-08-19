package com.github.frozensync

import com.github.frozensync.command.CommandRegistry
import com.github.frozensync.games.wordsnake.WordSnakeCommandSet
import com.github.frozensync.games.wordsnake.wordSnakeModule
import com.github.frozensync.monitoring.MonitoringCommandSet
import com.github.frozensync.persistence.mongodb.mongoModule
import com.github.frozensync.utility.FunCommandSet
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main() = runBlocking<Unit> {
    val koinApplication = startKoin {
        environmentProperties()
        modules(wordSnakeModule, mongoModule)
    }
    val koin = koinApplication.koin

    val token = koin.getProperty("YGGDRASIL_TOKEN") ?: exitProcess(1)
    val client = DiscordClientBuilder(token).build()

    val commandRepository = CommandRegistry
        .register(MonitoringCommandSet())
        .register(FunCommandSet())
        .register(koin.get<WordSnakeCommandSet>())

    client.eventDispatcher.on(MessageCreateEvent::class.java).asFlow()
        .filter { event -> event.message.author.map { !it.isBot }.orElse(false) }
        .onEach { event ->
            logger.trace { event.message.content.orElse("") }

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
