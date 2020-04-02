package com.github.frozensync.games.wordsnake

import com.github.frozensync.command.Command
import com.github.frozensync.command.CommandArgs
import com.github.frozensync.command.CommandSet
import reactor.core.publisher.Mono

object WordSnakeCommandSet : CommandSet {

    private const val GAME_STILL_ONGOING_MESSAGE = "There is already an on-going game in this channel"
    private const val NO_ONGOING_GAME_MESSAGE =
        "There is no on-going game in this channel. Please create one before playing."

    private val eventService: EventService = EventServiceImpl

    private val wordSnakeRepository: WordSnakeRepository = InMemoryWordSnakeRepository
    private val wordSnakeStatusRepository: WordSnakeStatusRepository = InMemoryWordSnakeStatusRepository

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["newgame"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    wordSnakeRepository.findByChannel(0L)
                        .map { GAME_STILL_ONGOING_MESSAGE }
                        .switchIfEmpty(
                            Mono.justOrEmpty(event.message.content)
                                .map { content ->
                                    val playerNames = CommandArgs(content).asWords()
                                    CreateGameCommand(playerNames)
                                }
                                .flatMap { command ->
                                    val gameCreatedEvent = WordSnake().handle(command)
                                    eventService.save(gameCreatedEvent)
                                    Mono.just(gameCreatedEvent)
                                }
                                .flatMap { wordSnakeStatusRepository.findByChannel(0L) }
                                .map { status ->
                                    """Created a new game with the following players: ${status.playerNames.joinToString()}
                                        |Turn ${status.turn}: ${status.currentPlayer.name}
                                    """.trimMargin()
                                }
                        )
                        .flatMap { message -> channel.createMessage(message) }
                }
                .then()
        }

        this["n"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    Mono.justOrEmpty(event.message.content)
                        .map { content ->
                            val word = CommandArgs(content).nextWord()
                            AppendWordCommand(word)
                        }
                        .flatMap { command ->
                            wordSnakeRepository.findByChannel(0L)
                                .map { game -> game.handle(command) }
                                .doOnNext { event -> eventService.save(event) }
                                .flatMap { wordSnakeStatusRepository.findByChannel(0L) }
                                .map { status ->
                                    """Word: ${status.lastWord}
                                        |Turn ${status.turn}: ${status.currentPlayer.name}
                                    """.trimMargin()
                                }
                                .switchIfEmpty(Mono.just(NO_ONGOING_GAME_MESSAGE))
                        }
                        .onErrorResume(InvalidWordException::class.java) { Mono.justOrEmpty(it.message) }
                        .flatMap { message -> channel.createMessage(message) }
                }
                .then()
        }

        this["undo"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    Mono.just(UndoTurnCommand())
                        .flatMap { command ->
                            wordSnakeRepository.findByChannel(0L)
                                .map { game -> game.handle(command) }
                                .doOnNext { event -> eventService.save(event) }
                                .flatMap { event ->
                                    wordSnakeStatusRepository.findByChannel(0L)
                                        .map { status ->
                                            """Undone ${event.removedWord}
                                                |Word: ${status.lastWord ?: ""}
                                                |Turn ${status.turn}: ${status.currentPlayer.name}
                                            """.trimMargin()
                                        }
                                }
                                .switchIfEmpty(Mono.just(NO_ONGOING_GAME_MESSAGE))
                        }
                        .onErrorResume(NoSuchWordException::class.java) { Mono.justOrEmpty(it.message) }
                        .flatMap { message -> channel.createMessage(message) }
                }
                .then()
        }

        this["currentturn"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    wordSnakeStatusRepository.findByChannel(0L)
                        .map { status ->
                            """Word: ${status.lastWord}
                                |Turn ${status.turn}: ${status.currentPlayer.name}
                            """.trimMargin()
                        }.flatMap { message -> channel.createMessage(message) }
                }
                .then()
        }

        this["snakestats"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    wordSnakeStatusRepository.findByChannel(0L)
                        .map { status ->
                            """Size: ${status.numberOfCharacters}
                                |Number of words: ${status.turn - 1}
                            """.trimMargin()
                        }.flatMap { message -> channel.createMessage(message) }
                }
                .then()
        }
    }
}
