package com.github.frozensync.games.shiritori

import com.github.frozensync.validation.Errors
import com.github.frozensync.validation.Validator
import mu.KotlinLogging

internal object ShiritoriValidator : Validator<Shiritori> {

    private val logger = KotlinLogging.logger { }

    override fun validate(target: Shiritori, errors: Errors): Errors {
        logger.entry(target, errors)
        var result = errors

        if (target.words.size + 1 != target.turn)
            result = errors.rejectValue("words", "A valid game must have (turns - 1) amount of words.")
        if (target.players.size < 2 && target.turn == 1)
            result = errors.rejectValue("players", "A starting game must have at least 2 players.")

        return logger.exit(result)
    }
}
