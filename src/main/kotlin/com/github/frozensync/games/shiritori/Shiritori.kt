package com.github.frozensync.games.shiritori

import kotlinx.collections.immutable.persistentSetOf
import org.bson.codecs.pojo.annotations.BsonId
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

private val DICTIONARY: Set<String> by lazy {
    Thread.currentThread().contextClassLoader.getResource("basiswoorden-gekeurd.txt")
        ?.let { Path.of(it.toURI()) }
        ?.let { Files.lines(it).collect(Collectors.toUnmodifiableSet()) }
        ?: throw IllegalStateException("Cannot load dictionary")
}

internal data class Shiritori(
    @BsonId val id: Long,
    val players: List<Player>,
    val currentPlayer: Player? = players.firstOrNull(),
    val words: Set<String> = persistentSetOf(),
    val currentWord: String? = null,
    val turn: Int = 1,
    @Transient val timer: DisqualificationTimer?,
) {
    suspend fun appendWord(word: String): Shiritori {
        when {
            isFinished() -> return this
            currentWord != null && currentWord.last() != word.first() -> throw InvalidWordException(""""$word" does not start with the last letter of "$currentWord".""")
            words.contains(word) -> throw InvalidWordException(""""$word" has already been used.""")
            !DICTIONARY.contains(word) -> throw InvalidWordException(""""$word" is not in the dictionary.""")
        }

        timer?.start(id)

        return copy(
            currentPlayer = nextPlayer(),
            words = words + word,
            currentWord = word,
            turn = turn + 1
        )
    }

    private fun nextPlayer(): Player? {
        if (players.size < 2) return currentPlayer
        if (currentPlayer == null) return players.first()

        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = if (currentIndex == players.size - 1) 0 else currentIndex + 1
        return players[nextIndex]
    }

    /**
     * Returns a new instance with [player] removed from the game. Returns the same instance if the game is already finished.
     */
    fun removePlayer(player: Player? = currentPlayer): Shiritori {
        if (player == null || isFinished()) return this

        return copy(
            players = players - player,
            currentPlayer = if (player == currentPlayer) nextPlayer() else currentPlayer
        )
    }

    /**
     * Returns true if the game is finished; false otherwise.
     */
    fun isFinished() = players.size == 1
}
