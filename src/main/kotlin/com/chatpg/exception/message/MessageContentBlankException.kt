package com.chatpg.exception.message

import com.chatpg.exception.http.BadRequestException

class MessageContentBlankException(message: String): BadRequestException(message)