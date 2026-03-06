package com.chatpg.exception.http

import com.chatpg.exception.LoggableException
import org.slf4j.event.Level

open class HttpException(
    val status: Int,
    message: String,
    level: Level = Level.WARN,
    internalMessage: String = message,
): LoggableException(level, message, internalMessage)