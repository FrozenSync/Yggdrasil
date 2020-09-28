package com.github.frozensync.music

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer

internal val PLAYER_MANAGER = DefaultAudioPlayerManager().apply {
    configuration.setFrameBufferFactory(::NonAllocatingAudioFrameBuffer)
    AudioSourceManagers.registerRemoteSources(this)
    AudioSourceManagers.registerLocalSource(this)
}
