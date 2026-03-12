package com.chatpg.exception.email

import com.chatpg.exception.http.sc4xx.ConflictException

class EmailAlreadyRegisteredException(message: String) : ConflictException(message)
