package com.github.frozensync.games.wordsnake

import kotlinx.serialization.Serializable

@Serializable
internal data class Player(val name: String, var isEliminated: Boolean = false)