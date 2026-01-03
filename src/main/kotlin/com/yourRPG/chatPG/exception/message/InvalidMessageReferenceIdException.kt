package com.yourRPG.chatPG.exception.message

import com.yourRPG.chatPG.exception.BadRequestException

class InvalidMessageReferenceIdException(message: String): BadRequestException(message)