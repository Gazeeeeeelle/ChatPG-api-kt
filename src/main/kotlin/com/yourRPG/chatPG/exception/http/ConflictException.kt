package com.yourRPG.chatPG.exception.http

open class ConflictException(message: String): HttpException(409, message)