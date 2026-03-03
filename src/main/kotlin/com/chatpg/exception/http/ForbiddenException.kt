package com.chatpg.exception.http

open class ForbiddenException(message: String): HttpException(403, message)