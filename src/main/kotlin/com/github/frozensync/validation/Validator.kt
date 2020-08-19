package com.github.frozensync.validation

interface Validator<T> {
    fun validate(target: T, errors: Errors = Errors()): Errors
}
