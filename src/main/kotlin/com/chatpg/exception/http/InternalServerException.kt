package com.chatpg.exception.http

import org.slf4j.event.Level

class InternalServerException(message: String): HttpException(500, message, Level.ERROR)