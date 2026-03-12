package com.chatpg.exception.http.sc4xx

import com.chatpg.exception.http.HttpException

open class NotFoundException(
    message: String,
    internalMessage: String = message,
) : HttpException(
    404,
    message,
    internalMessage = internalMessage
)