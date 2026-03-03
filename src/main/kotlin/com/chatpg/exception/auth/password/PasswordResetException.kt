package com.chatpg.exception.auth.password

import com.chatpg.exception.http.ConflictException

class PasswordResetException(message: String): ConflictException(message)