package com.chatpg.exception.chat

import com.chatpg.exception.http.sc4xx.ForbiddenException

class ForbiddenAccessToChatException(message: String): ForbiddenException(message)
