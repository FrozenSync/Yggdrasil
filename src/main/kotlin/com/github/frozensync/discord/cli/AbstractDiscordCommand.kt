package com.github.frozensync.discord.cli

import com.github.ajalt.clikt.core.*

abstract class AbstractDiscordCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
) : CliktCommand(
    help,
    epilog,
    name
) {
    fun execute(argv: List<String> = emptyList()) {
        try {
            parse(argv)
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
    }
}
