package com.chatpg.exception.auth.username

import com.chatpg.exception.http.sc4xx.BadRequestException

open class BadUsernameException(message: String) : BadRequestException(message)