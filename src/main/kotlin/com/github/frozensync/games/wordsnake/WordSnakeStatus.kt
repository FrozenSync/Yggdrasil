package com.github.frozensync.games.wordsnake

internal data class WordSnakeStatus(
    val players: List<Player>,
    val currentPlayer: Player,
    val playerNames: List<String> = players.map { it.name },
    val lastWord: String? = null,
    val numberOfCharacters: Int = 0,
    val turn: Int = 1
)
