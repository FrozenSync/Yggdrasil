package com.github.frozensync.games.wordsnake

import kotlinx.serialization.Serializable

@Serializable
internal sealed class Event

@Serializable
internal data class GameCreatedEvent(val channelId: Long, val players: List<Player>, val nextPlayer: Player) : Event()

@Serializable
internal data class WordAppendedEvent(val channelId: Long, val word: String, val nextPlayer: Player) : Event()

@Serializable
internal data class WordUndoneEvent(
    val channelId: Long,
    val removedWord: String,
    val currentWord: String?,
    val nextPlayer: Player
) : Event()
