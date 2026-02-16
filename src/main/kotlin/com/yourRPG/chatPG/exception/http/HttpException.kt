package com.yourRPG.chatPG.exception.http

open class HttpException(val status: Int, message: String): RuntimeException(message)