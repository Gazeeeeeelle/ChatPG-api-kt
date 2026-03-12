package com.chatpg.exception.http.sc4xx

import com.chatpg.exception.http.HttpException

open class ForbiddenException(message: String): HttpException(403, message)