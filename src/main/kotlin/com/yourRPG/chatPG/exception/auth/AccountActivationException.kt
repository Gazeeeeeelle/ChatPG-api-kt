package com.yourRPG.chatPG.exception.auth

import com.yourRPG.chatPG.exception.http.ConflictException

class AccountActivationException(message: String) : ConflictException(message)