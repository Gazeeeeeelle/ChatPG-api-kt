package com.chatpg.exception.chat

import com.chatpg.exception.http.sc4xx.NotFoundException

class ChatNotFoundException(message: String): NotFoundException(message)