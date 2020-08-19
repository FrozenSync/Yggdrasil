package com.github.frozensync.validation

import kotlinx.collections.immutable.*

data class Errors(val errors: PersistentList<Error> = persistentListOf()) {

    fun hasErrors() = errors.isNotEmpty()

    fun rejectValue(field: String, reason: String) = copy(errors = errors + Error(field, reason))

    fun getReasons() = errors.fold(StringBuilder()) { reasons, error -> reasons.append(error.reason) }.toString()
}

data class Error(val field: String, val reason: String)
