package com.github.frozensync.persistence.mongodb

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mongoModule = module {
    single { KMongo.createClient(getProperty("MONGODB_URI")).coroutine.getDatabase("default") }
}
