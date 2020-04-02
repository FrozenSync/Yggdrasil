package com.github.frozensync.games.wordsnake

import mu.KotlinLogging
import reactor.core.publisher.Mono

internal interface WordSnakeStatusRepository {
    fun findByChannel(channelId: Long): Mono<WordSnakeStatus>
    fun save(status: WordSnakeStatus)

    fun on(event: GameCreatedEvent)
    fun on(event: WordAppendedEvent)
    fun on(event: WordUndoneEvent)
}

internal object InMemoryWordSnakeStatusRepository : WordSnakeStatusRepository {

    private val logger = KotlinLogging.logger {}

    private val map = mutableMapOf<Long, WordSnakeStatus>()

    override fun findByChannel(channelId: Long): Mono<WordSnakeStatus> = Mono.defer {
        val result = map[channelId]
        if (result == null) Mono.empty() else Mono.just(result)
    }

    override fun save(status: WordSnakeStatus) {
        map[0L] = status
    }

    override fun on(event: GameCreatedEvent) {
        map[0L] = WordSnakeStatus(event.players, event.players.first())
    }

    override fun on(event: WordAppendedEvent) {
        val status = map[0L] ?: return
        val nextPlayer = status.nextPlayer()

        map[0L] = status.copy(
            currentPlayer = nextPlayer,
            lastWord = event.word,
            numberOfCharacters = status.numberOfCharacters + event.word.length,
            turn = status.turn + 1
        )
    }

    override fun on(event: WordUndoneEvent) {
        val status = map[0L] ?: return
        val nextPlayer = status.nextPlayer()

        map[0L] = status.copy(
            currentPlayer = nextPlayer,
            lastWord = event.currentWord,
            numberOfCharacters = status.numberOfCharacters - event.removedWord.length,
            turn = status.turn - 1
        )
    }
}
