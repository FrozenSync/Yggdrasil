package com.github.frozensync.games.wordsnake

internal class InvalidWordException(message: String) : IllegalArgumentException(message)
internal class NoSuchWordException(message: String) : IllegalStateException(message)
