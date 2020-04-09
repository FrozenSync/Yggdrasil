package com.github.frozensync.games.wordsnake

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

val DICTIONARY: Set<String> by lazy {
    Thread.currentThread().contextClassLoader.getResource("basiswoorden-gekeurd.txt")
        ?.let { Path.of(it.toURI()) }
        ?.let { Files.lines(it).collect(Collectors.toUnmodifiableSet()) }
        ?: throw IllegalStateException("Cannot load dictionary")
}
