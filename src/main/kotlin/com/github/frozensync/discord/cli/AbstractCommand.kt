package com.github.frozensync.discord.cli

import com.github.ajalt.clikt.core.*
import discord4j.core.event.domain.message.MessageCreateEvent
import mu.KotlinLogging

abstract class AbstractCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
) : CliktCommand(
    help,
    epilog,
    name
) {
    private val logger = KotlinLogging.logger { }

    private val invokedCommands by findObject<MutableList<AbstractCommand>>()

    abstract suspend fun execute(event: MessageCreateEvent)

    fun parse(message: String): AbstractCommand? {
        val argv = message.split(" ").dropPrefix().also { logger.debug { "argv=$it" } }

        try {
            parse(argv)
            return this
        } catch (e: ProgramResult) {
            echo(e.statusCode)
        } catch (e: PrintHelpMessage) {
            echo(e.command.getFormattedHelp())
        } catch (e: PrintCompletionMessage) {
            val s = if (e.forceUnixLineEndings) "\n" else currentContext.console.lineSeparator
            echo(e.message, lineSeparator = s)
        } catch (e: PrintMessage) {
            echo(e.message)
        } catch (e: UsageError) {
            echo(e.helpMessage(), err = true)
        } catch (e: CliktError) {
            echo(e.message, err = true)
        } catch (e: Abort) {
            echo(currentContext.localization.aborted(), err = true)
        }

        return null
    }

    private fun List<String>.dropPrefix() = drop(1)

    override fun run() {
        invokedCommands?.add(this)
    }
}
