package com.chatpg.exception.auth.username

import com.chatpg.exception.http.ConflictException

class UsernameAlreadyRegisteredException(message: String) : ConflictException(message)