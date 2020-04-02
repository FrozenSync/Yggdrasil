package com.github.frozensync.games.wordsnake

import reactor.core.publisher.Mono

internal interface WordSnakeRepository {
    fun findByChannel(channelId: Long): Mono<WordSnake>

    fun on(event: GameCreatedEvent)
    fun on(event: WordAppendedEvent)
    fun on(event: WordUndoneEvent)
}

internal object InMemoryWordSnakeRepository : WordSnakeRepository {

    private val map = mutableMapOf<Long, WordSnake>()

    override fun findByChannel(channelId: Long): Mono<WordSnake> = Mono.defer {
        val result = map[channelId]
        if (result == null) Mono.empty() else Mono.just(result)
    }

    override fun on(event: GameCreatedEvent) {
        map[0L] = WordSnake().also { it.apply(event) }
    }

    override fun on(event: WordAppendedEvent) {
        map[0L]?.apply(event)
    }

    override fun on(event: WordUndoneEvent) {
        map[0L]?.apply(event)
    }
}
