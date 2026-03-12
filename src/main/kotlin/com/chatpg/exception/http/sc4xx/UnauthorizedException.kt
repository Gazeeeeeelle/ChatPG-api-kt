package com.chatpg.exception.http.sc4xx

import com.chatpg.exception.http.HttpException
import org.slf4j.event.Level

open class UnauthorizedException(
    message: String = "Unauthorized",
    level: Level = Level.WARN,
    internalMessage: String = message
): HttpException(
    401,
    message,
    level,
    internalMessage
)