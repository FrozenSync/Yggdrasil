package com.github.frozensync.games.wordsnake

import org.koin.dsl.module

val wordSnakeModule = module {
    single { WordSnakeCommandSet(get(), get()) }

    single { WordSnakeRepositoryMongoDB(get()) }
    single<WordSnakeRepository> { WordSnakeCachedMongoRepository(get()) }
}