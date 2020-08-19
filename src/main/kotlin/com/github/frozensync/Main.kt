package com.github.frozensync

import com.github.frozensync.command.CommandHandler
import com.github.frozensync.games.wordsnake.wordSnakeModule
import com.github.frozensync.persistence.mongodb.mongoModule
import discord4j.core.DiscordClient
import discord4j.core.event.domain.Event
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.context.startKoin
import reactor.core.publisher.Flux
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main() = runBlocking<Unit> {
    val koinApplication = startKoin {
        environmentProperties()
        modules(wordSnakeModule, mongoModule)
    }
    val koin = koinApplication.koin
    val token = koin.getProperty("YGGDRASIL_TOKEN") ?: run {
        logger.error { "Environment variable not found: YGGDRASIL_TOKEN" }
        exitProcess(1)
    }

    DiscordClient.create(token).withGateway { client ->
        mono {
            launch { client.on(MessageCreateEvent::class.java).addListener(CommandHandler::executeCommands) }
        }
    }.block()
}

private suspend fun <E : Event> Flux<E>.addListener(action: suspend (Flow<E>) -> Unit) = action.invoke(asFlow())
