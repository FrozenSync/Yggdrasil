package com.github.frozensync.games.wordsnake

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

internal class DisqualificationTimer(
    private val time: Long, // ms
    private val wordSnakeRepository: WordSnakeRepository,
    private val notificationChannel: Channel<WordSnake>,
) {
    private val action: suspend (Long) -> Unit = { id ->
        wordSnakeRepository.findById(id)
            ?.removePlayer()
            ?.let {
                wordSnakeRepository.save(it)
                notificationChannel.send(it)
            }
    }

    private var job: Deferred<Unit>? = null

    suspend fun start(id: Long) {
        job?.cancel()
        job = GlobalScope.async {
            delay(time)
            action.invoke(id)
        }
    }
}
