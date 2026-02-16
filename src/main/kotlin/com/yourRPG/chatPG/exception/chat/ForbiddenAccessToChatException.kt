package com.yourRPG.chatPG.exception.chat

import com.yourRPG.chatPG.exception.http.ForbiddenException

class ForbiddenAccessToChatException(message: String): ForbiddenException(message)
