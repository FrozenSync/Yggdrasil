package com.github.frozensync.games.wordsnake

internal interface WordSnakeRepository {
    suspend fun findById(id: Long): WordSnake?
    suspend fun existsById(id: Long): Boolean
    suspend fun save(game: WordSnake)
    suspend fun delete(game: WordSnake)
}
