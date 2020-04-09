package com.github.frozensync.games.wordsnake

internal data class CreateGameCommand(val channelId: Long, val players: List<Player>)
internal data class AppendWordCommand(val channelId: Long, val player: Player, val word: String)
internal data class UndoWordCommand(val channelId: Long, val player: Player)
