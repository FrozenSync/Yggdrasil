package com.github.frozensync.games.wordsnake

import com.github.frozensync.CHANNEL_ID
import com.github.frozensync.USER_ID
import com.github.frozensync.USER_ID2
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.*

@Suppress("unused")
internal object WordSnakeTest : Spek({
    Feature("Word snake") {
        val initGame by memoized {
            val players = listOf(Player(USER_ID), Player(USER_ID2))
            WordSnake(CHANNEL_ID, players)
        }

        Scenario("player appends a valid word") {
            val word = "aap"
            lateinit var result: WordSnake

            When("a player appends a word that's present in the dictionary") {
                result = initGame.appendWord("aap")
            }

            Then("the word is the new current word") {
                assertEquals(word, result.currentWord)
            }

            And("the turn goes to another player") {
                assertNotEquals(initGame.currentPlayer, result.currentPlayer)
            }

            And("the turn number increments") {
                assertEquals(initGame.turn + 1, result.turn)
            }
        }

        Scenario("player appends a word that does not start with the last letter of the current word") {
            lateinit var game: WordSnake

            Given("a non-initial game") {
                game = initGame.appendWord("aap")
            }

            lateinit var result: Throwable

            When("a player appends a word that does not start with the last letter of the current word") {
                result = assertFails { game.appendWord("mens") }
            }

            Then("it should fail") {
                assertNotNull(result)
            }
        }

        Scenario("player appends a word that has already been used") {
            val word1 = "ster"
            val word2 = "ras"
            lateinit var game: WordSnake

            Given("a non-initial game") {
                game = initGame.appendWord(word1).appendWord(word2)
            }

            lateinit var result: Throwable

            When("a player appends a word that has already been used") {
                result = assertFails { game.appendWord(word1) }
            }

            Then("it should fail") {
                assertNotNull(result)
            }
        }

        Scenario("player appends a word that's not present in the dictionary") {
            val word = "gasovens"
            lateinit var result: Throwable

            When("a player appends a word that's not present in the dictionary") {
                result = assertFails { initGame.appendWord(word) }
            }

            Then("it should fail") {
                assertNotNull(result)
            }
        }

        Scenario("round passes") {
            lateinit var result: WordSnake

            When("all players have had their turn") {
                result = initGame.appendWord("aap").appendWord("peer")
            }

            Then("it should be the first player's turn again") {
                assertEquals(initGame.currentPlayer, result.currentPlayer)
            }
        }

        Scenario("player forfeits") {
            lateinit var forfeitingPlayer: Player
            lateinit var result: WordSnake

            When("a player forfeits") {
                forfeitingPlayer = initGame.currentPlayer
                result = initGame.removePlayer(forfeitingPlayer)
            }

            Then("he should be removed from the game") {
                assertFalse { result.players.contains(forfeitingPlayer) }
            }

            And("turn should move to the next player") {
                assertNotEquals(forfeitingPlayer, result.currentPlayer)
            }
        }

        Scenario("player wins by forfeit") {
            lateinit var result: WordSnake

            Given("a two-player game") {
                // initGame is already a two-player game
            }

            When("a player forfeits") {
                result = initGame.removePlayer()
            }

            Then("it should have one remaining player") {
                assertTrue { result.players.size == 1 }
            }

            And("the game is finished") {
                assertTrue { result.isFinished() }
            }
        }
    }
})
