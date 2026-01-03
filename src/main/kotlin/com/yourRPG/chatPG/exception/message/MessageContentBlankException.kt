package com.yourRPG.chatPG.exception.message

import com.yourRPG.chatPG.exception.BadRequestException

class MessageContentBlankException(message: String): BadRequestException(message)