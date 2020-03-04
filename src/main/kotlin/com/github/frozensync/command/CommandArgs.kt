package com.github.frozensync.command

import java.util.*

class CommandArgs(commandMessage: String) {

    private val args = commandMessage.substringAfter(" ")
    private val scanner by lazy { Scanner(args) }

    fun asString() = args
    fun asWords() = args.split(" ")
    fun nextWord() = scanner.next()
}