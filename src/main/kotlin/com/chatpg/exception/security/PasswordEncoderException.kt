package com.chatpg.exception.security

import com.chatpg.exception.http.HttpException
import org.slf4j.event.Level

class PasswordEncoderException(
    message: String,
    internalMessage: String
) : HttpException(500, message, level = Level.ERROR, internalMessage)