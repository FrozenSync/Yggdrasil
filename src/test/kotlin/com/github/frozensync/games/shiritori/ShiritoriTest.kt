package com.github.frozensync.games.shiritori

import com.github.frozensync.CHANNEL_ID
import com.github.frozensync.USER_ID
import com.github.frozensync.USER_ID2
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.*

@Suppress("unused")
internal object ShiritoriTest : Spek({
    Feature("Word snake") {
        val subject by memoized {
            val players = listOf(Player(USER_ID), Player(USER_ID2))
            Shiritori(CHANNEL_ID, players)
        }

        Scenario("player appends a valid word") {
            val word = "aap"
            lateinit var result: Shiritori

            When("a player appends a word that's present in the dictionary") {
                result = subject.appendWord("aap")
            }

            Then("the word is the new current word") {
                assertEquals(word, result.currentWord)
            }

            And("the turn goes to another player") {
                assertNotEquals(subject.currentPlayer, result.currentPlayer)
            }

            And("the turn number increments") {
                assertEquals(subject.turn + 1, result.turn)
            }
        }

        Scenario("player appends a word that does not start with the last letter of the current word") {
            lateinit var game: Shiritori

            Given("a non-initial game") {
                game = subject.appendWord("aap")
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
            lateinit var game: Shiritori

            Given("a non-initial game") {
                game = subject.appendWord(word1).appendWord(word2)
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
                result = assertFails { subject.appendWord(word) }
            }

            Then("it should fail") {
                assertNotNull(result)
            }
        }

        Scenario("round passes") {
            lateinit var result: Shiritori

            When("all players have had their turn") {
                result = subject.appendWord("aap").appendWord("peer")
            }

            Then("it should be the first player's turn again") {
                assertEquals(subject.currentPlayer, result.currentPlayer)
            }
        }

        Scenario("player forfeits") {
            lateinit var forfeitingPlayer: Player
            lateinit var result: Shiritori

            When("a player forfeits") {
                forfeitingPlayer = subject.currentPlayer!!
                result = subject.removePlayer(forfeitingPlayer)
            }

            Then("he should be removed from the game") {
                assertFalse { result.players.contains(forfeitingPlayer) }
            }

            And("turn should move to the next player") {
                assertNotEquals(forfeitingPlayer, result.currentPlayer)
            }
        }

        Scenario("player wins by forfeit") {
            lateinit var result: Shiritori

            Given("a two-player game") {
                // initGame is already a two-player game
            }

            When("a player forfeits") {
                result = subject.removePlayer()
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
