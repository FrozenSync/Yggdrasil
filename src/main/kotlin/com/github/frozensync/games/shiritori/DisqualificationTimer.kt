package com.github.frozensync.games.shiritori

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

internal class DisqualificationTimer(
    private val time: Long, // ms
    private val shiritoriRepository: ShiritoriRepository,
    private val notificationChannel: Channel<Shiritori>,
) {
    private val action: suspend (Long) -> Unit = { id ->
        shiritoriRepository.findById(id)
            ?.removePlayer()
            ?.let {
                shiritoriRepository.save(it)
                notificationChannel.send(it)
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
}
