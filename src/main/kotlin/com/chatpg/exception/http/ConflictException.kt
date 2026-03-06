package com.chatpg.exception.http

open class ConflictException(message: String): HttpException(409, message)