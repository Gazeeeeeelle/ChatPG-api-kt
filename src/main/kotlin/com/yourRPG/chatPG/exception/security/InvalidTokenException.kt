package com.yourRPG.chatPG.exception.security

import com.yourRPG.chatPG.exception.http.BadRequestException

class InvalidTokenException(message: String) : BadRequestException(message)