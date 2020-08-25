package com.github.frozensync.games.shiritori

import java.util.concurrent.ConcurrentHashMap

internal class ShiritoriCachedMongoRepository(
    private val mongoRepository: ShiritoriMongoRepository
) : ShiritoriRepository {

    private val map = ConcurrentHashMap<Long, Shiritori>()

    override suspend fun findById(id: Long): Shiritori? = map[id] ?: mongoRepository.findById(id)

    override suspend fun existsById(id: Long): Boolean = map.containsKey(id) || mongoRepository.existsById(id)

    override suspend fun save(game: Shiritori) {
        map[game.id] = game
        mongoRepository.save(game)
    }

    override suspend fun delete(game: Shiritori) {
        map.remove(game.id)
        mongoRepository.delete(game)
    }
}
