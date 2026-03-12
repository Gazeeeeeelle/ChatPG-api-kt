package com.chatpg.exception.http.sc5xx

import com.chatpg.exception.http.HttpException

open class ServiceUnavailableException(message: String): HttpException(503, message)