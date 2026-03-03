package com.chatpg.exception.email

import com.chatpg.exception.http.ConflictException

class EmailAlreadyRegisteredException(message: String) : ConflictException(message)
