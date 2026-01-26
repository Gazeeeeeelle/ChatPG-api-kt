package com.yourRPG.chatPG.exception.security

import com.yourRPG.chatPG.exception.UnauthorizedException

class InvalidTokenException(message: String?) : UnauthorizedException(message)