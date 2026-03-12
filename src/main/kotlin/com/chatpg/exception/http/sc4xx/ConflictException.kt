package com.chatpg.exception.http.sc4xx

import com.chatpg.exception.http.HttpException

open class ConflictException(message: String): HttpException(409, message)