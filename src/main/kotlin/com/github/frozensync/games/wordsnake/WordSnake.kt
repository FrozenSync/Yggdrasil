package com.github.frozensync.games.wordsnake

import java.util.*

internal class WordSnake {

    private val playerQueue: Deque<Player> = ArrayDeque()

    private val words: MutableSet<String> = LinkedHashSet(256)
    private var _lastWord: String? = null

    fun handle(command: CreateGameCommand): GameCreatedEvent {
        val players = command.playerNames.map { Player(it) }
        players.forEach { playerQueue.offer(it) }

        return GameCreatedEvent(players, playerQueue.peek())
    }

    fun handle(command: AppendWordCommand): WordAppendedEvent {
        val word = command.word
        val lastWord = _lastWord

        when {
            word.isBlank() -> throw InvalidWordException("Word \"$word\" cannot be blank.")
            lastWord != null && !word.startsWithLastLetterOf(lastWord) -> throw InvalidWordException("Word \"$word\" does not start with the last letter of \"$lastWord\".")
            words.contains(word) -> throw InvalidWordException("Word \"$word\" has already been used.")
        }

        appendWord(word)
        nextTurn()

        return WordAppendedEvent(word, playerQueue.peek())
    }

    private fun String.startsWithLastLetterOf(s: String) = first() == s.last()

    private fun appendWord(word: String) {
        words += word
        _lastWord = word
    }

    private fun nextTurn() {
        val player = playerQueue.remove()
        playerQueue.offer(player)
    }

    fun handle(@Suppress("UNUSED_PARAMETER") command: UndoTurnCommand): TurnUndoneEvent {
        if (words.isEmpty()) throw NoSuchWordException("There are no words to undo")

        val (removedWord, lastWord) = undoWord()
        val nextPlayer = undoTurn()

        return TurnUndoneEvent(removedWord, lastWord, nextPlayer)
    }

    private fun undoWord(): Pair<String, String?> {
        val wordToRemove = words.last()
        words.remove(wordToRemove)

        val lastWord = if (words.isEmpty()) null else words.last()
        _lastWord = lastWord

        return Pair(wordToRemove, lastWord)
    }

    private fun undoTurn(): Player {
        val player = playerQueue.removeLast()
        playerQueue.offerFirst(player)

        return playerQueue.peek()
    }

    fun apply(event: GameCreatedEvent) {
        event.players.forEach { playerQueue.offer(it) }
    }

    fun apply(event: WordAppendedEvent) {
        appendWord(event.word)
        nextTurn()
    }

    fun apply(@Suppress("UNUSED_PARAMETER") event: TurnUndoneEvent) {
        undoWord()
        undoTurn()
    }
}
