package com.github.frozensync.games.shiritori

import org.koin.dsl.module

val shiritoriModule = module {
    single { GameOverListener(get(), get()) }

    single { ShiritoriMongoRepository(get()) }
    single<ShiritoriRepository> { ShiritoriCachedMongoRepository(get()) }
}
