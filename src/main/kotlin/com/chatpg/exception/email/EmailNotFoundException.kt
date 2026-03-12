package com.chatpg.exception.email

import com.chatpg.exception.http.sc4xx.NotFoundException

class EmailNotFoundException(message: String): NotFoundException(message)