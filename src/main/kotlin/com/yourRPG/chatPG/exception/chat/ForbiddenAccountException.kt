package com.yourRPG.chatPG.exception.chat

import com.yourRPG.chatPG.exception.http.ForbiddenException

class ForbiddenAccountException(message: String): ForbiddenException(message)