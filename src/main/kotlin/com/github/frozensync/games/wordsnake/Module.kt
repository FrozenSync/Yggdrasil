package com.github.frozensync.games.wordsnake

import org.koin.dsl.module

val wordSnakeModule = module {
    single<WordSnakeRepository> { WordSnakeRepositoryMongoDB(get()) }
    single { WordSnakeCommandSet(get()) }
}