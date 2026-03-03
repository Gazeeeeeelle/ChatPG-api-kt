package com.chatpg.exception.email

import com.chatpg.exception.http.NotFoundException

class EmailNotFoundException(message: String): NotFoundException(message)