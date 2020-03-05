package com.github.frozensync.games.wordsnake

import com.github.frozensync.command.Command
import com.github.frozensync.command.CommandArgs
import com.github.frozensync.command.CommandSet
import reactor.core.publisher.Mono

object WordSnakeCommandSet : CommandSet {

    private lateinit var game: WordSnakeGame

    override val commands: Map<String, Command> = mutableMapOf<String, Command>().apply {
        this["newgame"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    Mono.justOrEmpty(event.message.content)
                        .map { content -> CommandArgs(content).asWords() }
                        .doOnNext { playerNames -> game = WordSnakeGame(playerNames) }
                        .flatMap { channel.createMessage(game.currentPrompt) }
                }
                .then()
        }

        this["n"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    Mono.justOrEmpty(event.message.content)
                        .map { content -> CommandArgs(content).nextWord() }
                        .doOnNext { word -> game.next(word) }
                        .flatMap { channel.createMessage(game.currentPrompt) }
                }
                .then()
        }

        this["currentturn"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    val result = StringBuilder().appendln(game.lastWord).append(game.currentTurn).toString()
                    channel.createMessage(result)
                }
                .then()
        }

        this["snakestats"] = { event ->
            event.message.channel
                .flatMap { channel ->
                    val statistics = game.getStatistics()
                    val result =
                        """Size: ${statistics.size}
                            |Number of words: ${statistics.numberOfWords}
                        """.trimMargin()
                    channel.createMessage(result)
                }
                .then()
        }
    }
}
