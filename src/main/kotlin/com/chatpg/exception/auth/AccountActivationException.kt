package com.chatpg.exception.auth

import com.chatpg.exception.http.ConflictException

class AccountActivationException(message: String) : ConflictException(message)