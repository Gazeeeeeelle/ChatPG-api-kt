package com.yourRPG.chatPG.exception.http

open class BadRequestException(message: String): HttpException(400, message)