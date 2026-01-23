package com.yourRPG.chatPG.exception.chat

import com.yourRPG.chatPG.exception.UnauthorizedException

class ForbiddenAccountException(message: String): UnauthorizedException(message)