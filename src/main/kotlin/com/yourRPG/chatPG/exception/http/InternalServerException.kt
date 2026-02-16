package com.yourRPG.chatPG.exception.http

class InternalServerException(message: String): HttpException(500, message)