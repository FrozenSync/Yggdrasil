package com.github.frozensync.discord.command

import java.util.*

class CommandArgs(commandMessage: String) {

    private val args = commandMessage.substringAfter(" ")
    private val scanner by lazy { Scanner(args) }

    fun asString() = args
    fun nextWord(): String? = if (scanner.hasNext()) scanner.next() else null
    fun split(delimiter: String = " ") = args.split(delimiter)
}