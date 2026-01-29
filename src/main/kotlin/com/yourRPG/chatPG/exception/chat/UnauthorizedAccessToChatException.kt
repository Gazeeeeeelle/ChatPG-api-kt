package com.yourRPG.chatPG.exception.chat

import com.yourRPG.chatPG.exception.http.UnauthorizedException

class UnauthorizedAccessToChatException(message: String): UnauthorizedException(message)
