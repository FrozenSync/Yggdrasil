package com.github.frozensync.command

import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

typealias Command = (MessageCreateEvent) -> Mono<Void>
