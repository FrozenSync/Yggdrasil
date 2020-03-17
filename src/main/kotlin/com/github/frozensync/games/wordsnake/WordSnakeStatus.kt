package com.github.frozensync.games.wordsnake

internal class WordSnakeStatus {

    lateinit var players: List<Player>
        private set
    lateinit var playerNames: List<String>
        private set
    lateinit var currentPlayer: Player
        private set

    var lastWord: String? = null
        private set
    var numberOfCharacters = 0
        private set

    var turn = 1
        private set

    fun on(event: GameCreatedEvent) {
        players = event.players
        playerNames = event.players.map { it.name }
        currentPlayer = event.nextPlayer
    }

    fun on(event: WordAppendedEvent) {
        currentPlayer = event.nextPlayer

        lastWord = event.word
        numberOfCharacters += event.word.length

        turn += 1
    }

    fun on(event: TurnUndoneEvent) {
        currentPlayer = event.nextPlayer

        lastWord = event.lastWord
        numberOfCharacters -= event.removedWord.length

        turn -= 1
    }
}
