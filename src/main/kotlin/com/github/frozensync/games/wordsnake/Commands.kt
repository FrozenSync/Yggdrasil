package com.github.frozensync.games.wordsnake

internal data class CreateGameCommand(val playerNames: List<String>)
internal data class AppendWordCommand(val word: String)
internal class UndoTurnCommand
