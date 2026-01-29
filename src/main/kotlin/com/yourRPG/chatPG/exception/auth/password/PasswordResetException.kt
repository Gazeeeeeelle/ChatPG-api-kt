package com.yourRPG.chatPG.exception.auth.password

import com.yourRPG.chatPG.exception.http.ConflictException

class PasswordResetException(message: String) : ConflictException(message)