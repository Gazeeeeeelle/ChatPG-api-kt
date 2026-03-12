package com.chatpg.exception.message

import com.chatpg.exception.http.sc4xx.NotFoundException

class MessageNotFoundException(message: String): NotFoundException(message)