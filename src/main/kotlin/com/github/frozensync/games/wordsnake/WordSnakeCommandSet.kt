package com.github.frozensync.games.wordsnake

import com.github.frozensync.command.Command
import com.github.frozensync.command.CommandArgs
import com.github.frozensync.command.CommandSet
import reactor.core.publisher.Mono

object WordSnakeCommandSet : CommandSet {

    private lateinit var game: WordSnake
    private lateinit var gameStatus: WordSnakeStatus

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["newgame"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    Mono.justOrEmpty(event.message.content)
                        .map { content ->
                            val playerNames = CommandArgs(content).asWords()
                            CreateGameCommand(playerNames)
                        }
                        .doOnNext {
                            game = WordSnake()
                            gameStatus = WordSnakeStatus()
                        }
                        .map { command -> game.handle(command) }
                        // .doOnNext { save event }
                        .doOnNext { event -> gameStatus.on(event) }
                        .map { "Created a new game with the following players: ${gameStatus.playerNames.joinToString()}" }
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
                            val command = AppendWordCommand(word)
                            game.handle(command)
                        }
                        // .doOnNext { save event }
                        .doOnNext { event -> gameStatus.on(event) }
                        .map {
                            """Word: ${gameStatus.lastWord}
                                |Turn ${gameStatus.turn}: ${gameStatus.currentPlayer.name}
                            """.trimMargin()
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
                        .map { command -> game.handle(command) }
                        // .doOnNext { save event }
                        .doOnNext { event -> gameStatus.on(event) }
                        .map { event ->
                            """Undone ${event.removedWord}
                                |Word: ${gameStatus.lastWord ?: ""}
                                |Turn ${gameStatus.turn}: ${gameStatus.currentPlayer.name}
                            """.trimMargin()
                        }
                        .onErrorResume(NoSuchWordException::class.java) { Mono.justOrEmpty(it.message) }
                        .flatMap { message -> channel.createMessage(message) }
                }
                .then()
        }

        this["currentturn"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    val message =
                        """Word: ${gameStatus.lastWord}
                            |Turn ${gameStatus.turn}: ${gameStatus.currentPlayer.name}
                        """.trimMargin()
                    channel.createMessage(message)
                }
                .then()
        }

        this["snakestats"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    val result =
                        """Size: ${gameStatus.numberOfCharacters}
                            |Number of words: ${gameStatus.turn - 1}
                        """.trimMargin()
                    channel.createMessage(result)
                }
                .then()
        }
    }
}
