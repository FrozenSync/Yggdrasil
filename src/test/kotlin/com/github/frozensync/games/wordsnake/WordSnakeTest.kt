package com.github.frozensync.games.wordsnake

import com.github.frozensync.CHANNEL_ID
import com.github.frozensync.CHANNEL_ID2
import com.github.frozensync.USER_ID
import com.github.frozensync.USER_ID2
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.*

@Suppress("unused")
internal object WordSnakeTest : Spek({
    Feature("Word snake") {
        val players = listOf(Player(USER_ID), Player(USER_ID2))
        val gameCreatedEvent = GameCreatedEvent(CHANNEL_ID, players, players[0])
        val game by memoized { WordSnake(gameCreatedEvent) }

        Scenario("user creates a two-player game") {
            val command = CreateGameCommand(CHANNEL_ID, players)
            var result: GameCreatedEvent? = null

            When("a user creates a game with two players") {
                result = WordSnake.handle(command)
            }

            Then("it should contain the same two players") {
                assertEquals(command.players, result?.players)
            }

            And("it should be in the same channel") {
                assertEquals(command.channelId, result?.channelId)
            }
        }

        Scenario("user creates a one-player game") {
            val command = CreateGameCommand(CHANNEL_ID, players.take(1))
            var result: GameCreatedEvent? = null

            When("a user creates a game with one player") {
                result = WordSnake.handle(command)
            }

            Then("it should fail") {
                assertNull(result)
            }
        }

        Scenario("player takes a turn") {
            val command = AppendWordCommand(CHANNEL_ID, players[0], "aap")
            lateinit var result: WordAppendedEvent

            When("a player takes a turn with a valid word") {
                result = game.handle(command)
            }

            Then("the valid word is the new current word") {
                assertEquals(command.word, result.word)
            }

            And("it should be another player's turn") {
                assertNotEquals(gameCreatedEvent.nextPlayer, result.nextPlayer)
            }
        }

        Scenario("a round passes") {
            val command1 = AppendWordCommand(CHANNEL_ID, players[0], "aap")
            val command2 = AppendWordCommand(CHANNEL_ID, players[1], "peer")
            lateinit var result: WordAppendedEvent

            When("a full round passes") {
                game.handle(command1).also { game.apply(it) }
                result = game.handle(command2).also { game.apply(it) }
            }

            Then("it should be the first player's turn again") {
                assertEquals(players[0], result.nextPlayer)
            }
        }

        Scenario("player takes a turn while it's not his turn") {
            val command = AppendWordCommand(CHANNEL_ID2, players[1], "aap")
            lateinit var result: Throwable

            When("a player takes a turn while it's not his turn") {
                result = assertFails { game.handle(command) }
            }

            Then("it should fail") {
                assertTrue { result is IllegalArgumentException }
            }
        }

        Scenario("player takes a turn in the wrong channel") {
            val command = AppendWordCommand(CHANNEL_ID2, players[0], "aap")
            lateinit var result: Throwable

            When("a player takes a turn in the wrong channel") {
                result = assertFails { game.handle(command) }
            }

            Then("it should fail") {
                assertTrue { result is IllegalArgumentException }
            }
        }

        Scenario("player takes a turn with a word that's not in the dictionary") {
            val command = AppendWordCommand(CHANNEL_ID2, players[0], "gasovens")
            lateinit var result: Throwable

            When("player appends a word that's not in the dictionary") {
                result = assertFails { game.handle(command) }
            }

            Then("it should fail") {
                assertNotNull(result)
            }
        }

        Scenario("player undoes a turn during the initial state") {
            val command = UndoWordCommand(CHANNEL_ID, players[0])
            var result: WordUndoneEvent? = null

            When("a player undoes a turn during the initial state") {
                result = game.handle(command)
            }

            Then("it should fail") {
                assertNull(result)
            }
        }

        Scenario("player undoes a turn resulting in an initial state") {
            val appendCommand = AppendWordCommand(CHANNEL_ID, players[0], "aap")
            lateinit var appendEvent: WordAppendedEvent

            Given("a non-initial game state with one word") {
                appendEvent = game.handle(appendCommand).also { game.apply(it) }
            }

            val undoCommand = UndoWordCommand(CHANNEL_ID, players[1])
            var result: WordUndoneEvent? = null

            When("a player undoes a turn resulting in an initial state") {
                result = game.handle(undoCommand)
            }

            Then("current word should be blank") {
                assertEquals(appendEvent.word, result?.removedWord)
                assertNull(result?.currentWord)
            }

            And("it should be the previous player's turn") {
                assertEquals(gameCreatedEvent.nextPlayer, result?.nextPlayer)
            }
        }

        Scenario("player undoes a turn") {
            val appendCommand1 = AppendWordCommand(CHANNEL_ID, players[0], "aap")
            val appendCommand2 = AppendWordCommand(CHANNEL_ID, players[1], "peer")
            lateinit var appendEvent1: WordAppendedEvent
            lateinit var appendEvent2: WordAppendedEvent

            Given("a non-initial game state with two words") {
                appendEvent1 = game.handle(appendCommand1).also { game.apply(it); }
                appendEvent2 = game.handle(appendCommand2).also { game.apply(it); }
            }

            val undoCommand = UndoWordCommand(CHANNEL_ID, players[0])
            var result: WordUndoneEvent? = null

            When("player undoes a turn") {
                result = game.handle(undoCommand)
            }

            Then("current word should revert back to the previous turn") {
                assertEquals(appendEvent2.word, result?.removedWord)
                assertEquals(appendEvent1.word, result?.currentWord)
            }

            And("it should be the previous player's turn") {
                assertEquals(appendEvent1.nextPlayer, result?.nextPlayer)
            }
        }

        Scenario("player undoes a turn while it's not his turn") {
            val command = UndoWordCommand(CHANNEL_ID, players[1])
            lateinit var result: Throwable

            When("a player undoes a turn while it's not his turn") {
                result = assertFails { game.handle(command) }
            }

            Then("it should fail") {
                assertTrue { result is IllegalArgumentException }
            }
        }
    }
})
