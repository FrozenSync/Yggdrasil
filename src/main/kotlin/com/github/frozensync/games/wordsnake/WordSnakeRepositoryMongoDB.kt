package com.github.frozensync.games.wordsnake

import mu.KotlinLogging
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

internal class WordSnakeRepositoryMongoDB(db: CoroutineDatabase) : WordSnakeRepository {

    private val logger = KotlinLogging.logger { }

    private val wordSnakeCollection = db.getCollection<WordSnake>()

    override suspend fun findById(id: Long): WordSnake? {
        logger.entry(id)
        val result = wordSnakeCollection.findOneById(id)
        return logger.exit(result)
    }

    override suspend fun existsById(id: Long): Boolean {
        logger.entry(id)
        val result = wordSnakeCollection.countDocuments(WordSnake::id eq id) > 0L
        return logger.exit(result)
    }

    override suspend fun save(game: WordSnake) {
        logger.entry(game)
        val result = wordSnakeCollection.save(game)
        logger.exit(result)
    }

    override suspend fun delete(game: WordSnake) {
        logger.entry(game)
        val result = wordSnakeCollection.deleteOneById(game.id)
        logger.exit(result)
    }
}
