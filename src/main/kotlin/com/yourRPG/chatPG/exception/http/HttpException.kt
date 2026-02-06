package com.yourRPG.chatPG.exception.http

abstract class HttpException(val status: Int, message: String): RuntimeException(message)