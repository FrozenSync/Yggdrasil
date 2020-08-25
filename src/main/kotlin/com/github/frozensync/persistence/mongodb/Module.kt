package com.github.frozensync.persistence.mongodb

import org.koin.dsl.module

val mongoModule = module {
    single { MongoClientFactory.get() }
}
