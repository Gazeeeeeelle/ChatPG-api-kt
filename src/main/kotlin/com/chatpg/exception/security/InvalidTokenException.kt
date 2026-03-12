package com.chatpg.exception.security

import com.chatpg.exception.http.sc4xx.BadRequestException

class InvalidTokenException(message: String) : BadRequestException(message)