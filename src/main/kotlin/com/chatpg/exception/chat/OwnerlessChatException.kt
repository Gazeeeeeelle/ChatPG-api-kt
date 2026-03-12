package com.chatpg.exception.chat

import com.chatpg.exception.http.sc4xx.BadRequestException

class OwnerlessChatException(message: String) : BadRequestException(message)