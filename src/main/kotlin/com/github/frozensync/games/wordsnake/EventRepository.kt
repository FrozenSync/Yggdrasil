package com.github.frozensync.games.wordsnake

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Paths

internal interface EventRepository {
    fun save(event: Event): Mono<Void>
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

    override fun save(event: Event): Mono<Void> {
        data.events += event

        val result = json.stringify(Data.serializer(), data)
        Files.writeString(path, result)

        return Mono.empty()
    }
}
