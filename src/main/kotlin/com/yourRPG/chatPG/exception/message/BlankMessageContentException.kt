package com.yourRPG.chatPG.exception.message

import com.yourRPG.chatPG.exception.BadRequestException

class BlankMessageContentException(message: String): BadRequestException(message)