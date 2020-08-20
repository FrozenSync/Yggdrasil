package com.github.frozensync.games.wordsnake

import java.util.concurrent.ConcurrentHashMap

internal class WordSnakeCachedMongoRepository(
    private val mongoRepository: WordSnakeRepositoryMongoDB
) : WordSnakeRepository {

    private val map = ConcurrentHashMap<Long, WordSnake>()

    override suspend fun findById(id: Long): WordSnake? = map[id] ?: mongoRepository.findById(id)

    override suspend fun existsById(id: Long): Boolean = map.containsKey(id) || mongoRepository.existsById(id)

    override suspend fun save(game: WordSnake) {
        map[game.id] = game
        mongoRepository.save(game)
    }

    override suspend fun delete(game: WordSnake) {
        map.remove(game.id)
        mongoRepository.delete(game)
    }
}
