package com.github.frozensync.games.wordsnake

internal data class CreateGameCommand(val channelId: Long, val playerNames: List<String>)
internal data class AppendWordCommand(val channelId: Long, val word: String)
internal data class UndoTurnCommand(val channelId: Long)
