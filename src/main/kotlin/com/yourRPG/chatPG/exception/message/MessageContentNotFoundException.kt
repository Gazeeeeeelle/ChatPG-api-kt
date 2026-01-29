package com.yourRPG.chatPG.exception.message

import com.yourRPG.chatPG.exception.http.NotFoundException

class MessageContentNotFoundException(message: String) : NotFoundException(message)