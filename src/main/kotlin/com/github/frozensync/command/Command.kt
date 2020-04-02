package com.github.frozensync.command

import discord4j.core.event.domain.message.MessageCreateEvent

typealias Command = suspend (MessageCreateEvent) -> Unit
