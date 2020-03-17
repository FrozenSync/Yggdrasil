package com.github.frozensync.games.wordsnake

internal data class CreateGameCommand(val playerNames: List<String>)
internal data class AppendWordCommand(val word: String)
internal class UndoTurnCommand

internal data class GameCreatedEvent(val players: List<Player>, val nextPlayer: Player)
internal data class WordAppendedEvent(val word: String, val nextPlayer: Player)
internal data class TurnUndoneEvent(val removedWord: String, val lastWord: String?, val nextPlayer: Player)
