package com.chatpg.exception.message

import com.chatpg.exception.http.NotFoundException

class MessageContentNotFoundException(message: String) : NotFoundException(message)