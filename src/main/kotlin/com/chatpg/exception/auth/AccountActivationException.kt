package com.chatpg.exception.auth

import com.chatpg.exception.http.sc4xx.ConflictException

class AccountActivationException(message: String) : ConflictException(message)