package com.github.frozensync.games.shiritori

import mu.KotlinLogging
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

internal class ShiritoriMongoRepository(db: CoroutineDatabase) : ShiritoriRepository {

    private val logger = KotlinLogging.logger { }

    private val shiritoriCollection = db.getCollection<Shiritori>()

    override suspend fun findById(id: Long): Shiritori? {
        logger.entry(id)
        val result = shiritoriCollection.findOneById(id)
        return logger.exit(result)
    }

    override suspend fun existsById(id: Long): Boolean {
        logger.entry(id)
        val result = shiritoriCollection.countDocuments(Shiritori::id eq id) > 0L
        return logger.exit(result)
    }

    override suspend fun save(game: Shiritori) {
        logger.entry(game)
        val result = shiritoriCollection.save(game)
        logger.exit(result)
    }

    override suspend fun delete(game: Shiritori) {
        logger.entry(game)
        val result = shiritoriCollection.deleteOneById(game.id)
        logger.exit(result)
    }
}
