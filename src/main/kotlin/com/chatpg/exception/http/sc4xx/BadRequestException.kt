package com.chatpg.exception.http.sc4xx

import com.chatpg.exception.http.HttpException

open class BadRequestException(message: String): HttpException(400, message)