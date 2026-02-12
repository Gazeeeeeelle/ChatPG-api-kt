package com.yourRPG.chatPG.exception.http

open class UnauthorizedException(message: String = "Unauthorized"): HttpException(401, message)