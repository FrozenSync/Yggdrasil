package com.github.frozensync

import com.github.frozensync.command.CommandRegistry
import com.github.frozensync.games.wordsnake.WordSnakeCommandSet
import com.github.frozensync.games.wordsnake.wordSnakeModule
import com.github.frozensync.monitoring.MonitoringCommandSet
import com.github.frozensync.persistence.mongodb.mongoModule
import com.github.frozensync.utility.FunCommandSet
import discord4j.core.DiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
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
    val commandRepository = CommandRegistry
        .register(MonitoringCommandSet())
        .register(FunCommandSet())
        .register(koin.get<WordSnakeCommandSet>())

    DiscordClient.create(token).withGateway { client ->
        mono {
            launch { client.on(MessageCreateEvent::class.java).asFlow()
                .filter { event -> event.message.author.map { !it.isBot }.orElse(false) }
                .onEach { event ->
                    logger.trace { event.message.content }

                    val commandName = event.message.content.parseCommandName()
                    val command = commandRepository.findByName(commandName)
                    command?.invoke(event)
                }
            }
        }
    }.block()
}

private fun String.parseCommandName() = this.substringBefore(' ').removePrefix("!")
