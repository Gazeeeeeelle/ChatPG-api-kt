package com.yourRPG.chatPG.exception.http

open class ServiceUnavailableException(message: String): HttpException(503, message)
