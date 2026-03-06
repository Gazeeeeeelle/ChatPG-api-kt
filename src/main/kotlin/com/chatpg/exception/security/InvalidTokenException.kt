package com.chatpg.exception.security

import com.chatpg.exception.http.BadRequestException

class InvalidTokenException(message: String) : BadRequestException(message)