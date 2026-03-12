package com.chatpg.exception.http.sc5xx

import com.chatpg.exception.http.HttpException
import org.slf4j.event.Level

class InternalServerException(message: String): HttpException(500, message, Level.ERROR)