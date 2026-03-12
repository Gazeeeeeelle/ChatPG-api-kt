package com.chatpg.exception.auth.password

import com.chatpg.exception.http.sc4xx.ConflictException

class PasswordResetException(message: String): ConflictException(message)