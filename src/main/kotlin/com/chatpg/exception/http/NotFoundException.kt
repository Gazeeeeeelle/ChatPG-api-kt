package com.chatpg.exception.http

open class NotFoundException(
    message: String,
    internalMessage: String = message,
) : HttpException(
    404,
    message,
    internalMessage = internalMessage
)