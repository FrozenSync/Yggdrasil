package com.github.frozensync.games.wordsnake

internal interface EventService {
    fun save(event: Event)
}

internal object EventServiceImpl : EventService {

    private val eventRepository: EventRepository = FileRepository
    private val wordSnakeRepository: WordSnakeRepository = InMemoryWordSnakeRepository
    private val wordSnakeStatusRepository: WordSnakeStatusRepository = InMemoryWordSnakeStatusRepository

    init {
        // TODO convert to event bus
        eventRepository.findAll().forEach { processEvent(it) }
    }

    override fun save(event: Event) {
        eventRepository.save(event)
        processEvent(event)
    }

    private fun processEvent(event: Event) {
        when (event) {
            is GameCreatedEvent -> {
                wordSnakeRepository.on(event)
                wordSnakeStatusRepository.on(event)
            }
            is WordAppendedEvent -> {
                wordSnakeRepository.on(event)
                wordSnakeStatusRepository.on(event)
            }
            is WordUndoneEvent -> {
                wordSnakeRepository.on(event)
                wordSnakeStatusRepository.on(event)
            }
        }
    }
}
