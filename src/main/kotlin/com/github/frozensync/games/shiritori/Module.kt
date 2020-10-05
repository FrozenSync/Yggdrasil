package com.github.frozensync.games.shiritori

import com.github.frozensync.discord.UserId
import discord4j.common.util.Snowflake
import discord4j.core.DiscordClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import org.koin.dsl.module

val shiritoriModule = module {
    single { createGameOverByTimerChannel(get(), get()) }

    single { ShiritoriMongoRepository(get()) }
    single<ShiritoriRepository> { ShiritoriCachedMongoRepository(get()) }
}

// TODO change to class
private fun createGameOverByTimerChannel(
    shiritoriRepository: ShiritoriRepository,
    discordClient: DiscordClient
): Channel<Shiritori> {
    val result = Channel<Shiritori>(Channel.BUFFERED)

    GlobalScope.launch {
        result.consumeEach {
            shiritoriRepository.delete(it)

            val channelId = Snowflake.of(it.id)
            val channel = discordClient.getChannelById(channelId)
            val message = createVictoryByTimerMessage(it)
            channel.createMessage(message).awaitFirst()
        }
    }

    return result
}

private fun createVictoryByTimerMessage(game: Shiritori) =
    "Congratulations ${game.currentPlayer?.id?.let { UserId(it) }}, you won!"
