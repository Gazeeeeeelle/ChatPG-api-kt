package com.yourRPG.chatPG.exception.chat

import com.yourRPG.chatPG.exception.BadRequestException

class OwnerlessChatException(message: String) : BadRequestException(message)