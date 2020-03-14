package com.github.frozensync

import com.github.frozensync.command.CommandRegistry
import com.github.frozensync.monitoring.MonitoringCommandSet
import com.github.frozensync.utility.FunCommandSet
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Please supply a Discord bot token in args.")
        exitProcess(1)
    }
    val token = args[0]
    val client = DiscordClientBuilder(token).build()

    val commandRepository = CommandRegistry
        .register(MonitoringCommandSet)
        .register(FunCommandSet)

    client.eventDispatcher.on(MessageCreateEvent::class.java)
        .filter { event -> event.message.author.map { !it.isBot }.orElse(false) }
        .flatMap { event ->
            Mono.justOrEmpty(event.message.content)
                .map { content -> content.substringBefore(' ').removePrefix("!") }
                .flatMap { commandName ->
                    val command = commandRepository.findByName(commandName)
                    if (command == null) Mono.empty() else Mono.just(command)
                }
                .flatMap { command -> command.invoke(event) }
        }
        .subscribe()

    client.login().block()
}
