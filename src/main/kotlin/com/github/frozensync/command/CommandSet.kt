package com.github.frozensync.command

interface CommandSet {
    val commands: Map<String, Command>
}
