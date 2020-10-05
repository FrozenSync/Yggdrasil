package com.github.frozensync.discord.cli

import discord4j.core.event.domain.message.MessageCreateEvent

abstract class AbstractCommandCategory(name: String? = null) : AbstractCommand(name = name) {
    override suspend fun execute(event: MessageCreateEvent) = Unit
}