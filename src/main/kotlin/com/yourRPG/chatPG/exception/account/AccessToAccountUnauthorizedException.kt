package com.yourRPG.chatPG.exception.account

import com.yourRPG.chatPG.exception.http.UnauthorizedException

class AccessToAccountUnauthorizedException(message: String): UnauthorizedException(message)