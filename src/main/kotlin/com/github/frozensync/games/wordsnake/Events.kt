package com.github.frozensync.games.wordsnake

import kotlinx.serialization.Serializable

@Serializable
internal sealed class Event

@Serializable
internal data class GameCreatedEvent(val players: List<Player>) : Event()

@Serializable
internal data class WordAppendedEvent(val word: String) : Event() // TODO add player

@Serializable
internal data class WordUndoneEvent(val removedWord: String, val currentWord: String) : Event()
