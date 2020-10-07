package com.github.frozensync.games.shiritori

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import com.github.frozensync.discord.UserId
import com.github.frozensync.command.cli.AbstractCommand
import com.github.frozensync.command.cli.AbstractCommandCategory
import discord4j.common.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.get

private const val GAME_FOUND = "Found an on-going game in this channel. Please finish it before creating a new one."
private const val NO_GAME_FOUND = "No game found in this channel. Please create one before playing."

class ShiritoriCategory : AbstractCommandCategory(name = "shiritori")

class NewGameCommand : AbstractCommand(), KoinComponent {

    private val logger = KotlinLogging.logger { }

    private val shiritoriRepository: ShiritoriRepository = get()
    private val gameOverListener: GameOverListener = get()

    private val timerDuration: Long by option("-t", "--timer", help = "Timer in ms").long().default(30000L)
    private val playerMentions: List<String> by argument().multiple(required = true)

    override suspend fun execute(event: MessageCreateEvent) {
        logger.debug { "players=$playerMentions" }

        val channel = event.message.channel.awaitFirst()
        val channelId = channel.id.asLong()

        if (shiritoriRepository.existsById(channelId)) {
            channel.createMessage(GAME_FOUND)
            return
        }

        val players = playerMentions
            .map { Snowflake.asLong(it.removeSurrounding("<@!", ">")) }
            .map { Player(it) }
        val timer = DisqualificationTimer(timerDuration, shiritoriRepository, gameOverListener)
        val result = Shiritori(channelId, players, timer = timer)
        val errors = ShiritoriValidator.validate(result)
        if (errors.hasErrors()) {
            channel.createMessage(errors.getReasons()).awaitSingle()
            return
        }
        shiritoriRepository.save(result)

        logger.info { "New game: $result" }

        val playerMentions = result.players.map { UserId(it.id).toString() }
        val message =
            """Created a new game with the following players: $playerMentions
                    |Turn 1: ${result.currentPlayer?.id?.let { UserId(it) }}
                """.trimMargin()
        channel.createMessage(message).awaitFirst()
    }
}

class PlayWordCommand : AbstractCommand(name = "play"), KoinComponent {

    private val shiritoriRepository: ShiritoriRepository = get()

    private val nextWord: String by argument()

    override suspend fun execute(event: MessageCreateEvent) {
        val player = event.message.author.map { Player(it.id.asLong()) }.orElse(null) ?: return
        val channel = event.message.channel.awaitFirst()
        val channelId = channel.id.asLong()

        val game = shiritoriRepository.findById(channelId)
        if (game == null) {
            channel.createMessage(NO_GAME_FOUND).awaitFirst()
            return
        }
        if (player != game.currentPlayer) {
            channel.createMessage("It's not your turn ${UserId(player.id)}!").awaitFirst()
            return
        }

        val message = try { // TODO replace with Either<>
            val result = game.appendWord(nextWord)
            shiritoriRepository.save(result)

            createTurnMessage(result)
        } catch (e: InvalidWordException) {
            e.message!!
        }
        channel.createMessage(message).awaitFirst()
    }
}

class ForfeitCommand : AbstractCommand(), KoinComponent {

    private val shiritoriRepository: ShiritoriRepository = get()

    override suspend fun execute(event: MessageCreateEvent) {
        val player = event.message.author.map { Player(it.id.asLong()) }.orElse(null) ?: return
        val channel = event.message.channel.awaitFirst()
        val channelId = channel.id.asLong()

        val game = shiritoriRepository.findById(channelId)
        if (game == null) {
            channel.createMessage(NO_GAME_FOUND).awaitFirst()
            return
        }

        val message = try {
            val result = game.removePlayer(player)
            if (result.isFinished()) {
                shiritoriRepository.delete(result)
                createVictoryMessage(result)
            } else {
                shiritoriRepository.save(result)
                createTurnMessage(result)
            }
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
        channel.createMessage(message).awaitFirst()
    }
}

class CurrentTurnCommand : AbstractCommand(), KoinComponent {

    private val shiritoriRepository: ShiritoriRepository = get()

    override suspend fun execute(event: MessageCreateEvent) {
        val channel = event.message.channel.awaitFirst()
        val channelId = channel.id.asLong()

        val message = when (val game = shiritoriRepository.findById(channelId)) {
            null -> NO_GAME_FOUND
            else -> createTurnMessage(game)
        }
        channel.createMessage(message).awaitFirst()
    }
}

private fun createTurnMessage(game: Shiritori) =
    """Word: ${game.currentWord}
            |Turn ${game.turn}: ${game.currentPlayer?.id?.let { UserId(it) }}
        """.trimMargin()

private fun createVictoryMessage(game: Shiritori) =
    "Congratulations ${game.currentPlayer?.id?.let { UserId(it) }}, you won!"