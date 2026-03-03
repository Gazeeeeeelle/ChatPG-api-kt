package com.chatpg.exception.chat

import com.chatpg.exception.http.ForbiddenException

class ForbiddenAccountException(message: String): ForbiddenException(message)