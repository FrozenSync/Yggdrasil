package com.github.frozensync

import com.github.frozensync.discord.command.CommandHandler
import com.github.frozensync.discord.discordModule
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

private val logger = KotlinLogging.logger { }

fun main() = runBlocking<Unit> {
    val koinApplication = startKoin {
        environmentProperties()
        modules(discordModule, wordSnakeModule, mongoModule)
    }
    val koin = koinApplication.koin

    koin.get<DiscordClient>().withGateway { client ->
        mono {
            launch { client.on(MessageCreateEvent::class.java).addListener(CommandHandler::executeCommands) }
        }
    }.block()
}

private suspend fun <E : Event> Flux<E>.addListener(action: suspend (Flow<E>) -> Unit) = action.invoke(asFlow())
