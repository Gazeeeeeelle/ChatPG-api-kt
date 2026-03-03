package com.chatpg.logging

import com.chatpg.exception.LoggableException
import com.chatpg.service.ai.providers.AiModel
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.event.Level

class LoggingUtils<S : Any>(
    singletonInstance: S
) {

    private val log: KLogger = KotlinLogging.logger(
        name = getName(singletonInstance)
            ?: throw IllegalArgumentException("Instance given does not have a qualified name.")
    )

    private fun getName(singletonInstance: S): String? =
        singletonInstance::class
            .takeIf { it.isCompanion }
            ?.java
            ?.enclosingClass
            ?.name

    fun (Int).ifZeroInteractedLogAndThrow(exceptionSupplier: () -> LoggableException) {
        if (this == 0) logAndThrow(exceptionSupplier)
    }

    fun <T : LoggableException> logAndThrow(exceptionSupplier: () -> T): Nothing {
        val ex = exceptionSupplier()

        at(ex.level) { ex.message }

        throw ex
    }

    fun <T : LoggableException> exception(loggableException: T) =
        at(loggableException.level) { loggableException.internalMessage }

    fun at(level: Level, message: String?) {
        val method: (() -> Any?) -> Unit = log.run {
            when (level) {
                Level.WARN  -> ::warn
                Level.ERROR -> ::error
                Level.INFO  -> ::info
                Level.DEBUG -> ::debug
                Level.TRACE -> ::trace
            }
        }

        method { message }
    }

    fun at(level: Level, messageSupplier: () -> String?) =
        at(level, messageSupplier())

    fun logAndThrowAt(level: Level, throwable: Throwable): AiModel {
        at(level) { throwable.message }
        throw throwable
    }

    fun logAndThrowAt(level: Level, throwableSupplier: () -> Throwable): AiModel =
        logAndThrowAt(level, throwableSupplier())

}