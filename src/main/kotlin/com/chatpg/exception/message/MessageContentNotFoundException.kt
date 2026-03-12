package com.chatpg.exception.message

import com.chatpg.exception.http.sc4xx.NotFoundException

class MessageContentNotFoundException(message: String) : NotFoundException(message)