package com.github.frozensync.games.wordsnake

import mu.KotlinLogging

internal interface WordSnakeStatusRepository {
    suspend fun findByChannel(channelId: Long): WordSnakeStatus?

    fun on(event: GameCreatedEvent)
    fun on(event: WordAppendedEvent)
    fun on(event: WordUndoneEvent)
}

internal object InMemoryWordSnakeStatusRepository : WordSnakeStatusRepository {

    private val logger = KotlinLogging.logger {}

    private val map = mutableMapOf<Long, WordSnakeStatus>()

    override suspend fun findByChannel(channelId: Long): WordSnakeStatus? = map[channelId]

    override fun on(event: GameCreatedEvent) {
        map[event.channelId] = WordSnakeStatus(event.players, event.players.first())
    }

    override fun on(event: WordAppendedEvent) {
        val status = map[event.channelId] ?: return
        val nextPlayer = status.nextPlayer()

        map[event.channelId] = status.copy(
            currentPlayer = nextPlayer,
            lastWord = event.word,
            numberOfCharacters = status.numberOfCharacters + event.word.length,
            turn = status.turn + 1
        )
    }

    override fun on(event: WordUndoneEvent) {
        val status = map[event.channelId] ?: return
        val nextPlayer = status.nextPlayer()

        map[event.channelId] = status.copy(
            currentPlayer = nextPlayer,
            lastWord = event.currentWord,
            numberOfCharacters = status.numberOfCharacters - event.removedWord.length,
            turn = status.turn - 1
        )
    }
}
