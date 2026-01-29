package com.yourRPG.chatPG.exception.email

import com.yourRPG.chatPG.exception.http.ConflictException

class EmailAlreadyRegisteredException(message: String) : ConflictException(message)
