package com.chatpg.exception.http

open class ServiceUnavailableException(message: String): HttpException(503, message)
