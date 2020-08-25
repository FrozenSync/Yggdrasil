package com.github.frozensync.persistence.mongodb

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.nio.file.Files
import java.nio.file.Path

object MongoClientFactory {

    fun get(): CoroutineDatabase {
        val path = System.getenv("MONGODB_URI_FILE")?.let { Path.of(it) } ?: Path.of("")

        return if (Files.exists(path)) {
            val uri = Files.readString(path)
            KMongo.createClient(uri).coroutine.getDatabase("default")
        } else {
            throw IllegalStateException("File not found at location specified by MONGODB_URI_FILE")
        }
    }
}