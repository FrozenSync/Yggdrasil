package com.github.frozensync.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class AudioTrackScheduler(private val player: AudioPlayer) : AudioEventAdapter(), AudioLoadResultHandler {

    private val logger = KotlinLogging.logger { }

    /* The queue may be modifed by different threads so guarantee memory safety
     * This does not, however, remove several race conditions currently present
     */
    private val _queue: Queue<AudioTrack> = ConcurrentLinkedQueue()
    val queue: List<AudioTrack>
        get() = _queue.toList()

    override fun trackLoaded(track: AudioTrack?) {
        play(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        _queue.addAll(playlist.tracks)
        resume()
    }

    override fun noMatches() {
        logger.warn { "No match found" }
    }

    override fun loadFailed(exception: FriendlyException) {
        logger.catching(exception)
    }

    /**
     * Advance the player if the track completed naturally (FINISHED) or if the track cannot play (LOAD_FAILED)
     */
    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            skip()
        }
    }

    private fun play(track: AudioTrack?, force: Boolean = false): Boolean {
        val playing = player.startTrack(track, !force)
        if (!playing && track != null) _queue.add(track)

        return playing
    }

    private fun resume() {
        val track = _queue.peek() ?: return
        val playing = player.startTrack(track, true)
        if (playing) _queue.remove(track)
    }

    private fun skip(): Boolean {
        return _queue.isNotEmpty() && play(_queue.poll(), true)
    }
}
