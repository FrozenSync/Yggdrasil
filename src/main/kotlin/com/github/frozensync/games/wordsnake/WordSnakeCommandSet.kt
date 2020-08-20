package com.github.frozensync.games.wordsnake

import com.github.frozensync.discord.command.Command
import com.github.frozensync.discord.command.CommandArgs
import com.github.frozensync.discord.command.CommandSet
import com.github.frozensync.discord.UserId
import discord4j.common.util.Snowflake
import discord4j.core.DiscordClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging

private const val GAME_FOUND = "Found an on-going game in this channel. Please finish it before creating a new one."
private const val NO_GAME_FOUND = "No game found in this channel. Please create one before playing."

internal class WordSnakeCommandSet(
    private val discordClient: DiscordClient,
    private val wordSnakeRepository: WordSnakeRepository
) : CommandSet {

    private val logger = KotlinLogging.logger { }

    private val gameOverNotifications = Channel<WordSnake>(Channel.BUFFERED)
    private val gameOverListener = GlobalScope.async {
        gameOverNotifications.consumeEach {
            logger.info { "Game over: $it" }

            wordSnakeRepository.delete(it)

            val channelId = Snowflake.of(it.id)
            val channel = discordClient.getChannelById(channelId)
            val message = createVictoryMessage(it)
            channel.createMessage(message).awaitSingle()
        }
    }

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["newgame"] = h@{ event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            if (wordSnakeRepository.existsById(channelId)) {
                channel.createMessage(GAME_FOUND)
                return@h
            }

            val players = event.message.userMentionIds.map { Player(it.asLong()) }
            val timer = DisqualificationTimer(3000L, wordSnakeRepository, gameOverNotifications)
            val result = WordSnake(channelId, players, timer = timer)
            val errors = WordSnakeValidator.validate(result)
            if (errors.hasErrors()) {
                channel.createMessage(errors.getReasons()).awaitSingle()
                return@h
            }
            wordSnakeRepository.save(result)

            logger.info { "New game: $result" }

            val playerMentions = result.players.map { UserId(it.id).toString() }
            val message =
                """Created a new game with the following players: $playerMentions
                    |Turn 1: ${result.currentPlayer?.id?.let { UserId(it) }}
                """.trimMargin()
            channel.createMessage(message).awaitFirst()
        }

        this["n"] = h@{ event ->
            val player = event.message.author.map { Player(it.id.asLong()) }.orElse(null) ?: return@h
            val word = event.message.content.let { CommandArgs(it).nextWord() } ?: return@h
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val game = wordSnakeRepository.findById(channelId)
            if (game == null) {
                channel.createMessage(NO_GAME_FOUND).awaitFirst()
                return@h
            }
            if (player != game.currentPlayer) {
                channel.createMessage("It's not your turn ${UserId(player.id)}!").awaitFirst()
                return@h
            }

            val message = try { // TODO replace with Either<>
                val result = game.appendWord(word)
                wordSnakeRepository.save(result)

                createTurnMessage(result)
            } catch (e: InvalidWordException) {
                e.message!!
            }
            channel.createMessage(message).awaitFirst()
        }

        this["forfeit"] = h@{ event ->
            val player = event.message.author.map { Player(it.id.asLong()) }.orElse(null) ?: return@h
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val game = wordSnakeRepository.findById(channelId)
            if (game == null) {
                channel.createMessage(NO_GAME_FOUND).awaitFirst()
                return@h
            }

            val message = try {
                val result = game.removePlayer(player)
                if (result.isFinished()) {
                    wordSnakeRepository.delete(result)
                    createVictoryMessage(result)
                } else {
                    wordSnakeRepository.save(result)
                    createTurnMessage(result)
                }
            } catch (e: IllegalArgumentException) {
                e.message!!
            }
            channel.createMessage(message).awaitFirst()
        }

        this["currentturn"] = { event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val message = when (val game = wordSnakeRepository.findById(channelId)) {
                null -> NO_GAME_FOUND
                else -> createTurnMessage(game)
            }
            channel.createMessage(message).awaitFirst()
        }

        this["snakestats"] = { event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val message = when (val game = wordSnakeRepository.findById(channelId)) {
                null -> NO_GAME_FOUND
                else -> {
                    val statistics = game.computeStatistics()
                    """Length: ${statistics.snakeLength}
                        |Number of words: ${statistics.numberOfWords}
                    """.trimMargin()
                }
            }
            channel.createMessage(message).awaitFirst()
        }
    }

    private fun createTurnMessage(game: WordSnake) =
        """Word: ${game.currentWord}
            |Turn ${game.turn}: ${game.currentPlayer?.id?.let { UserId(it) }}
        """.trimMargin()

    private fun createVictoryMessage(game: WordSnake) =
        "Congratulations ${game.currentPlayer?.id?.let { UserId(it) }}, you won!"
}
