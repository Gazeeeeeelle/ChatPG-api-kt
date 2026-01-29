package com.yourRPG.chatPG.exception.security

import com.yourRPG.chatPG.exception.http.UnauthorizedException

class InvalidTokenException(message: String) : UnauthorizedException(message)