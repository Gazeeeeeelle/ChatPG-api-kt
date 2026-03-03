package com.chatpg.exception.auth.username

import com.chatpg.exception.http.BadRequestException

open class BadUsernameException(message: String) : BadRequestException(message)