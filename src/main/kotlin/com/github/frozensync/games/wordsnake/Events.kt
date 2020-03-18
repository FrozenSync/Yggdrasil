package com.github.frozensync.games.wordsnake

import kotlinx.serialization.Serializable

@Serializable
internal sealed class Event

@Serializable
internal data class GameCreatedEvent(val players: List<Player>, val nextPlayer: Player) : Event()

@Serializable
internal data class WordAppendedEvent(val word: String, val nextPlayer: Player) : Event()

@Serializable
internal data class TurnUndoneEvent(val removedWord: String, val lastWord: String?, val nextPlayer: Player) : Event()
