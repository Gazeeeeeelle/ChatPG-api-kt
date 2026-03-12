package com.chatpg.exception.message

import com.chatpg.exception.http.sc4xx.BadRequestException

class MessageContentBlankException(message: String): BadRequestException(message)