package com.github.frozensync.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import discord4j.common.util.Snowflake
import java.util.concurrent.ConcurrentHashMap

internal class GuildAudioManager private constructor() {

    companion object {
        private val MANAGERS: MutableMap<Snowflake, GuildAudioManager> = ConcurrentHashMap()

        fun of(id: Snowflake): GuildAudioManager = MANAGERS.computeIfAbsent(id) { GuildAudioManager() }
    }

    val player: AudioPlayer = PLAYER_MANAGER.createPlayer()
    val scheduler: AudioTrackScheduler = AudioTrackScheduler(player)
    val provider: LavaPlayerAudioProvider = LavaPlayerAudioProvider(player)

    init {
        player.addListener(scheduler)
    }
}
