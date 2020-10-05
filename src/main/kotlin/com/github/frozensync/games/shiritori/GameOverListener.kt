package com.github.frozensync.games.shiritori

import com.github.frozensync.discord.UserId
import discord4j.common.util.Snowflake
import discord4j.core.DiscordClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import mu.KotlinLogging

internal class GameOverListener(
    private val shiritoriRepository: ShiritoriRepository,
    private val discordClient: DiscordClient
) {
    private val logger = KotlinLogging.logger { }

    private val notifications = Channel<Shiritori>(Channel.BUFFERED)

    init {
        GlobalScope.launch {
            notifications.consumeEach {
                logger.info { "Game over: $it" }

                shiritoriRepository.delete(it)

                val channelId = Snowflake.of(it.id)
                val channel = discordClient.getChannelById(channelId)
                val message = createVictoryByTimerMessage(it)
                channel.createMessage(message).awaitFirst()
            }
        }
    }

    suspend fun sendGameOver(game: Shiritori) {
        notifications.send(game)
    }

    private fun createVictoryByTimerMessage(game: Shiritori) =
        "Congratulations ${game.currentPlayer?.id?.let { UserId(it) }}, you won!"
}
