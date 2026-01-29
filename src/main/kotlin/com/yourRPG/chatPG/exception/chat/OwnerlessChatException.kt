package com.yourRPG.chatPG.exception.chat

import com.yourRPG.chatPG.exception.http.BadRequestException

class OwnerlessChatException(message: String) : BadRequestException(message)