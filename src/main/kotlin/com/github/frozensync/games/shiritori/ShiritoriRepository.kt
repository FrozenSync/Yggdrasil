package com.github.frozensync.games.shiritori

internal interface ShiritoriRepository {
    suspend fun findById(id: Long): Shiritori?
    suspend fun existsById(id: Long): Boolean
    suspend fun save(game: Shiritori)
    suspend fun delete(game: Shiritori)
}
