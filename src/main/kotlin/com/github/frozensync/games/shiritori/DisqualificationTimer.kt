package com.github.frozensync.games.shiritori

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

internal class DisqualificationTimer(
    private val time: Long, // ms
    private val shiritoriRepository: ShiritoriRepository,
    private val gameOverListener: GameOverListener,
) {
    private val action: suspend (Long) -> Unit = { id ->
        shiritoriRepository.findById(id)
            ?.removePlayer()
            ?.let {
                if (it.isFinished())
                    gameOverListener.sendGameOver(it)
                else
                    shiritoriRepository.save(it)
            }
    }

    private var job: Deferred<Unit>? = null

    fun start(id: Long) {
        job?.cancel()
        job = GlobalScope.async {
            delay(time)
            action.invoke(id)
        }
    }

    override fun toString(): String =
        "DisqualificationTimer{time=$time}"
}
