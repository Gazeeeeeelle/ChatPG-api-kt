package com.chatpg.exception.http

import org.slf4j.event.Level

abstract class FullDetailHttpException(
    status: Int,
    message: String,
    level: Level,
    internalMessage: String,
): HttpException(status, message, level, internalMessage)
