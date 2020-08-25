package com.github.frozensync.discord

import org.koin.dsl.module

val discordModule = module {
    single { DiscordClientFactory.get() }
}
