package com.chatpg.exception.chat

import com.chatpg.exception.http.BadRequestException

class OwnerlessChatException(message: String) : BadRequestException(message)