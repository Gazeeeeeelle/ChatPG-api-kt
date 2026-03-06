package com.chatpg.exception.message

import com.chatpg.exception.http.NotFoundException

class MessageNotFoundException(message: String): NotFoundException(message)