package com.yourRPG.chatPG.exception.http

open class NotFoundException(message: String): HttpException(404, message)