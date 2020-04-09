package com.github.frozensync.games.wordsnake

import com.github.frozensync.command.Command
import com.github.frozensync.command.CommandArgs
import com.github.frozensync.command.CommandSet
import com.github.frozensync.discord.UserId
import kotlinx.coroutines.reactive.awaitFirst

object WordSnakeCommandSet : CommandSet {

    private const val GAME_STILL_ONGOING_MESSAGE = "There is already an on-going game in this channel"
    private const val NO_ONGOING_GAME_MESSAGE =
        "There is no on-going game in this channel. Please create one before playing."

    private val eventService: EventService = EventServiceImpl

    private val wordSnakeRepository: WordSnakeRepository = InMemoryWordSnakeRepository
    private val wordSnakeStatusRepository: WordSnakeStatusRepository = InMemoryWordSnakeStatusRepository

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["newgame"] = h@{ event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val conflict = wordSnakeRepository.findByChannel(channelId) != null
            if (conflict) {
                channel.createMessage(GAME_STILL_ONGOING_MESSAGE)
                return@h
            }

            val createGameCommand = event.message.userMentionIds
                .map { Player(it.asLong()) }
                .let { CreateGameCommand(channelId, players = it) }

            val gameCreatedEvent = WordSnake.handle(createGameCommand)
            if (gameCreatedEvent == null) { // TODO replace with Either<>
                channel.createMessage("Cannot create a game with less than two players.").awaitFirst()
                return@h
            }
            eventService.save(gameCreatedEvent)

            val playerMentions = gameCreatedEvent.players.map { UserId(it.id).toString() }
            val message =
                """Created a new game with the following players: $playerMentions
                    |Turn 1: ${UserId(gameCreatedEvent.nextPlayer.id)}
                """.trimMargin()
            channel.createMessage(message).awaitFirst()
        }

        this["n"] = h@{ event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val game = wordSnakeRepository.findByChannel(channelId)
            if (game == null) {
                channel.createMessage(NO_ONGOING_GAME_MESSAGE).awaitFirst()
                return@h
            }

            val player = event.message.author.map { Player(it.id.asLong()) }.orElse(null) ?: return@h
            val appendWordCommand = event.message.content
                .map { AppendWordCommand(channelId, player, CommandArgs(it).nextWord()) }
                .orElse(null) ?: return@h

            val message = try { // TODO replace with Either<>
                val wordAppendedEvent = game.handle(appendWordCommand)
                eventService.save(wordAppendedEvent)

                val status = wordSnakeStatusRepository.findByChannel(channelId)
                """Word: ${wordAppendedEvent.word}
                    |Turn ${status?.turn}: ${UserId(wordAppendedEvent.nextPlayer.id)}
                """.trimMargin()
            } catch (e: InvalidWordException) {
                e.message!!
            }
            channel.createMessage(message).awaitFirst()
        }

        this["undo"] = h@{ event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val game = wordSnakeRepository.findByChannel(channelId)
            if (game == null) {
                channel.createMessage(NO_ONGOING_GAME_MESSAGE).awaitFirst()
                return@h
            }

            val player = event.message.author.map { Player(it.id.asLong()) }.orElse(null) ?: return@h
            val undoWordCommand = UndoWordCommand(channelId, player)
            val wordUndoneEvent = game.handle(undoWordCommand) ?: return@h
            eventService.save(wordUndoneEvent)

            val status = wordSnakeStatusRepository.findByChannel(channelId)
            val message =
                """Undone ${wordUndoneEvent.removedWord}
                    |Word: ${wordUndoneEvent.currentWord ?: ""}
                    |Turn ${status?.turn}: ${UserId(wordUndoneEvent.nextPlayer.id)}
                """.trimMargin()
            channel.createMessage(message).awaitFirst()
        }

        this["currentturn"] = { event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val message = wordSnakeStatusRepository.findByChannel(channelId)
                ?.let { status ->
                    """Word: ${status.lastWord}
                        |Turn ${status.turn}: ${UserId(status.currentPlayer.id)}
                    """.trimMargin()
                }
                ?: NO_ONGOING_GAME_MESSAGE
            channel.createMessage(message).awaitFirst()
        }

        this["snakestats"] = { event ->
            val channel = event.message.channel.awaitFirst()
            val channelId = channel.id.asLong()

            val message = wordSnakeStatusRepository.findByChannel(channelId)
                ?.let { status ->
                    """Size: ${status.numberOfCharacters}
                        |Number of words: ${status.turn - 1}
                    """.trimMargin()
                }
                ?: NO_ONGOING_GAME_MESSAGE
            channel.createMessage(message).awaitFirst()
        }
    }
}
