package com.yourRPG.chatPG.exception.auth.username

import com.yourRPG.chatPG.exception.http.BadRequestException

open class BadUsernameException(message: String) : BadRequestException(message)