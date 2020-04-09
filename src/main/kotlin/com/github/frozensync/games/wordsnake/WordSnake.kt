package com.github.frozensync.games.wordsnake

import java.util.*

internal class WordSnake(event: GameCreatedEvent) {

    private val channelId: Long = event.channelId

    private val words: MutableSet<String> = LinkedHashSet(256)
    private var _lastWord: String? = null

    private val players: List<Player> = event.players
    private var currentPlayer = event.nextPlayer

    companion object {
        fun handle(command: CreateGameCommand): GameCreatedEvent? {
            if (command.players.size < 2) return null

            return GameCreatedEvent(command.channelId, command.players, command.players[0])
        }
    }

    fun handle(command: AppendWordCommand): WordAppendedEvent {
        if (channelId != command.channelId) throw IllegalArgumentException("Wrong channel id")

        val word = command.word
        val lastWord = _lastWord

        when {
            word.isBlank() -> throw InvalidWordException("Word \"$word\" cannot be blank.")
            lastWord != null && !word.startsWithLastLetterOf(lastWord) -> throw InvalidWordException("Word \"$word\" does not start with the last letter of \"$lastWord\".")
            words.contains(word) -> throw InvalidWordException("Word \"$word\" has already been used.")
        }

        return WordAppendedEvent(command.channelId, word, players.nextPlayer(currentPlayer))
    }

    private fun String.startsWithLastLetterOf(s: String) = first() == s.last()

    private fun List<Player>.nextPlayer(currentPlayer: Player): Player {
        val currentIndex = indexOf(currentPlayer)
        val nextIndex = if (currentIndex == size - 1) 0 else currentIndex + 1
        return this[nextIndex]
    }

    fun handle(command: UndoWordCommand): WordUndoneEvent? {
        if (channelId != command.channelId) throw IllegalArgumentException("Wrong channel id")

        val lastWord = _lastWord ?: return null
        val currentWord = if (words.size == 1) null else words.last { it != lastWord }

        return WordUndoneEvent(command.channelId, lastWord, currentWord, players.previousPlayer(currentPlayer))
    }

    private fun List<Player>.previousPlayer(currentPlayer: Player): Player {
        val currentIndex = indexOf(currentPlayer)
        val nextIndex = if (currentIndex == 0) size - 1 else currentIndex - 1
        return this[nextIndex]
    }

    fun apply(event: WordAppendedEvent) {
        words += event.word
        _lastWord = event.word

        currentPlayer = event.nextPlayer
    }

    fun apply(event: WordUndoneEvent) {
        words -= event.removedWord
        _lastWord = event.currentWord

        currentPlayer = event.nextPlayer
    }
}
