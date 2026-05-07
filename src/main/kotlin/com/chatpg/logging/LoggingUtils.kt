package com.chatpg.logging

import com.chatpg.exception.LoggableException
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.event.Level

class LoggingUtils {

    private var name: String

    private var log: KLogger

    companion object {
        fun logger(emptyLambda: () -> Unit): LoggingUtils {
            val name = emptyLambda::class
                .qualifiedName
                ?.replaceAfter("$$", "")
                ?.removeSuffix("$$")

            return LoggingUtils(name ?: "null")
        }
    }

    private constructor(name: String) {
        this.name = name
        this.log  = KotlinLogging.logger(name)
    }

    fun <T : LoggableException> andThrow(exceptionSupplier: () -> T): Nothing {
        val ex = exceptionSupplier()

        at(ex.level) { ex.message }

        throw ex
    }

    fun <T : LoggableException> exception(loggableException: T) =
        at(loggableException.level) { loggableException.internalMessage }

    internal fun at(level: Level, message: String?) {
        val method: (() -> Any?) -> Unit =
            log.run {
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

    internal fun andThrowAt(level: Level, throwable: Throwable): Nothing {
        at(level) { throwable.message }
        throw throwable
    }

    fun andThrowAt(level: Level, throwableSupplier: () -> Throwable): Nothing =
        andThrowAt(level, throwableSupplier())

}