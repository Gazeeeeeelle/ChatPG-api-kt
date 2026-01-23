package com.yourRPG.chatPG.exception.auth.username

import com.yourRPG.chatPG.exception.ConflictException

class UsernameAlreadyRegisteredException(message: String) : ConflictException(message)