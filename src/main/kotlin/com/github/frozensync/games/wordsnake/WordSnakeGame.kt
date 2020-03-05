package com.github.frozensync.games.wordsnake

import java.util.*
import kotlin.collections.LinkedHashSet

internal class WordSnakeGame(playerNames: List<String>) {

    val currentTurn
        get() = "Turn $turn: ${playerQueue.peek().name}"
    val currentWord
        get() = "Word: $_lastWord"

    val logs = mutableListOf<String>()
    val currentPrompt
        get() = logs.last()

    private val players: List<Player> = playerNames.map { Player(it) }
    private val playerQueue: Deque<Player> = ArrayDeque(players)
    private var turn = 1

    private val words: MutableSet<String> = LinkedHashSet(128)
    private var _lastWord: String? = null
    private var numberOfCharacters = 0

    init {
        val initialLogBuilder = StringBuilder().appendln("A new game has been created with the following players:")
        playerNames.forEach { initialLogBuilder.appendln(it) }
        initialLogBuilder.appendln(currentTurn)
        val initialLog = initialLogBuilder.toString()
        logs.add(initialLog)
    }

    fun next(word: String): Boolean {
        if (!appendWord(word)) {
            return false
        }

        val player = playerQueue.remove()
        playerQueue.offer(player)
        turn += 1

        val log = StringBuilder().appendln(currentWord).appendln(currentTurn).toString()
        logs.add(log)

        return true
    }

    private fun appendWord(word: String): Boolean {
        if (word.isBlank()) {
            logs.add("Words cannot be blank")
            return false
        }
        val lastWord = _lastWord
        if (lastWord != null && lastWord.last() != word.first()) {
            logs.add("\"$word\" does not start with the last letter of \"$lastWord\"")
            return false
        }

        val added = words.add(word)
        if (!added) {
            logs.add("$word has already been used")
            return false
        }

        _lastWord = word
        numberOfCharacters += word.length

        return true
    }

    fun undo() {
        val lastWord = dropLastWord()

        val player = playerQueue.removeLast()
        playerQueue.offerFirst(player)
        turn -= 1

        val log = StringBuilder().appendln("Undone $lastWord").appendln(currentWord).appendln(currentTurn).toString()
        logs.add(log)
    }

    private fun dropLastWord(): String {
        val lastWord = words.last()
        words.remove(lastWord)

        _lastWord = words.last()
        numberOfCharacters -= lastWord.length

        return lastWord
    }

    fun getStatistics() = SnakeSummaryStatistics(numberOfCharacters, words.size)
}

internal data class Player(val name: String, var isEliminated: Boolean = false)

internal data class SnakeSummaryStatistics(val size: Int, val numberOfWords: Int)
