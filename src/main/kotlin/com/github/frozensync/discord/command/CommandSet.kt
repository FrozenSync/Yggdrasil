package com.github.frozensync.discord.command

interface CommandSet {
    val commands: Map<String, Command>
}
