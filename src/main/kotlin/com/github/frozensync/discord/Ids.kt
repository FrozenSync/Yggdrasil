package com.github.frozensync.discord

inline class UserId(private val value: Long) {
    override fun toString(): String = "<@!$value>"
}

inline class ChannelId(private val value: Long) {
    override fun toString(): String = "<#$value>"
}
