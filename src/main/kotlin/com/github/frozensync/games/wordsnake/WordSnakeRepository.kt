package com.github.frozensync.games.wordsnake

internal interface WordSnakeRepository {
    suspend fun findByChannel(channelId: Long): WordSnake?

    fun on(event: GameCreatedEvent)
    fun on(event: WordAppendedEvent)
    fun on(event: WordUndoneEvent)
}

internal object InMemoryWordSnakeRepository : WordSnakeRepository {

    private val map = mutableMapOf<Long, WordSnake>()

    override suspend fun findByChannel(channelId: Long): WordSnake? = map[channelId]

    override fun on(event: GameCreatedEvent) {
        map[event.channelId] = WordSnake(event)
    }

    override fun on(event: WordAppendedEvent) {
        map[event.channelId]?.apply(event)
    }

    override fun on(event: WordUndoneEvent) {
        map[event.channelId]?.apply(event)
    }
}
