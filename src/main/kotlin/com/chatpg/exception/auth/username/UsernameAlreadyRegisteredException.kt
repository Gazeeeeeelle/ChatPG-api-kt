package com.chatpg.exception.auth.username

import com.chatpg.exception.http.sc4xx.ConflictException

class UsernameAlreadyRegisteredException(message: String) : ConflictException(message)