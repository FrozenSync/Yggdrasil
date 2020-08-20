package com.github.frozensync.discord.command

import discord4j.core.event.domain.message.MessageCreateEvent

typealias Command = suspend (MessageCreateEvent) -> Unit
