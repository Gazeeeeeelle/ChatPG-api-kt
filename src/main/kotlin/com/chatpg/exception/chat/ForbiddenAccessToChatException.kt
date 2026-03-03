package com.chatpg.exception.chat

import com.chatpg.exception.http.ForbiddenException

class ForbiddenAccessToChatException(message: String): ForbiddenException(message)
