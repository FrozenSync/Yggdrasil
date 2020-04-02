package com.github.frozensync.games.wordsnake

import java.util.*

internal class WordSnake {

    private val words: MutableSet<String> = LinkedHashSet(256)
    private var _lastWord: String? = null

    fun handle(command: CreateGameCommand): GameCreatedEvent {
        val players = command.playerNames.map { Player(it) }
        if (players.isEmpty()) throw IllegalArgumentException("Cannot create a game with no players")

        return GameCreatedEvent(players)
    }

    fun handle(command: AppendWordCommand): WordAppendedEvent {
        val word = command.word
        val lastWord = _lastWord

        when {
            word.isBlank() -> throw InvalidWordException("Word \"$word\" cannot be blank.")
            lastWord != null && !word.startsWithLastLetterOf(lastWord) -> throw InvalidWordException("Word \"$word\" does not start with the last letter of \"$lastWord\".")
            words.contains(word) -> throw InvalidWordException("Word \"$word\" has already been used.")
        }

        return WordAppendedEvent(word)
    }

    private fun String.startsWithLastLetterOf(s: String) = first() == s.last()

    fun handle(@Suppress("UNUSED_PARAMETER") command: UndoTurnCommand): WordUndoneEvent {
        val lastWord = _lastWord ?: throw NoSuchWordException("There are no words to undo")
        val currentWord = words.last { it != lastWord }
        return WordUndoneEvent(lastWord, currentWord)
    }

    fun apply(@Suppress("UNUSED_PARAMETER") event: GameCreatedEvent) {
        // do nothing
    }

    fun apply(event: WordAppendedEvent) {
        appendWord(event.word)
    }

    private fun appendWord(word: String) {
        words += word
        _lastWord = word
    }

    fun apply(@Suppress("UNUSED_PARAMETER") event: WordUndoneEvent) {
        undoWord()
    }

    private fun undoWord(): Pair<String, String?> {
        val wordToRemove = words.last()
        words.remove(wordToRemove)

        val lastWord = if (words.isEmpty()) null else words.last()
        _lastWord = lastWord

        return Pair(wordToRemove, lastWord)
    }
}
