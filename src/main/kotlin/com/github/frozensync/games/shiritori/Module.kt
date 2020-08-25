package com.github.frozensync.games.shiritori

import org.koin.dsl.module

val shiritoriModule = module {
    single { ShiritoriCommandSet(get(), get()) }

    single { ShiritoriMongoRepository(get()) }
    single<ShiritoriRepository> { ShiritoriCachedMongoRepository(get()) }
}