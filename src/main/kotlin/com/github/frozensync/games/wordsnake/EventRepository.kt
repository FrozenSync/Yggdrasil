package com.github.frozensync.games.wordsnake

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.nio.file.Files
import java.nio.file.Paths

internal interface EventRepository {
    fun findAll(): List<Event>
    fun save(event: Event)
}

@Serializable
private data class Data(val events: MutableList<Event> = mutableListOf())

internal object FileRepository : EventRepository {

    private val data: Data

    private val json = Json(JsonConfiguration.Stable)
    private val path = Paths.get("events.json")

    init {
        data = if (Files.exists(path)) {
            val input = Files.readString(path)
            if (input.isNotBlank()) json.parse(Data.serializer(), input) else Data()
        } else {
            Data()
        }
    }

    override fun findAll(): List<Event> = data.events

    override fun save(event: Event) {
        data.events += event

        val result = json.stringify(Data.serializer(), data)
        Files.writeString(path, result)
    }
}
