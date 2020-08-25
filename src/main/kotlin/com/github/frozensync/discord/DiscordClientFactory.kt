package com.github.frozensync.discord

import discord4j.core.DiscordClient
import java.nio.file.Files
import java.nio.file.Path

object DiscordClientFactory {

    fun get(): DiscordClient {
        val path = System.getenv("DISCORD_TOKEN_FILE")?.let { Path.of(it) } ?: Path.of("")

        return if (Files.exists(path)) {
            val token = Files.readString(path)
            DiscordClient.create(token)
        } else {
            throw IllegalStateException("File not found at location specified by DISCORD_TOKEN_FILE")
        }
    }
}