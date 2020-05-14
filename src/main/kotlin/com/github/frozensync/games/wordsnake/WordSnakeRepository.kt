package com.github.frozensync.games.wordsnake

internal interface WordSnakeRepository {
    suspend fun findById(id: Long): WordSnake?
    suspend fun exists(id: Long): Boolean
    suspend fun save(game: WordSnake)
}

internal class InMemoryWordSnakeRepository : WordSnakeRepository {

    private val map = mutableMapOf<Long, WordSnake>()

    override suspend fun findById(id: Long): WordSnake? = map[id]

    override suspend fun exists(id: Long) = map.contains(id)

    override suspend fun save(game: WordSnake) {
        map[game.id] = game
    }
}
