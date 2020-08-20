package com.github.frozensync.discord

import discord4j.core.DiscordClient
import org.koin.dsl.module

val discordModule = module {
    single { DiscordClient.create(getProperty("YGGDRASIL_TOKEN")) }
}
