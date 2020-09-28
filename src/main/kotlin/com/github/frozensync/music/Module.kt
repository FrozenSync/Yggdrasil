package com.github.frozensync.music

import org.koin.dsl.module

val musicModule = module {
    single { MusicCommandSet() }
}