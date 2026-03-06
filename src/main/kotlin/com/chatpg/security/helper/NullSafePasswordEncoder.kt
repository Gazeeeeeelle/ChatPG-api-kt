package com.chatpg.security.helper

import com.chatpg.exception.security.PasswordEncoderException
import com.chatpg.logging.LoggingUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class NullSafePasswordEncoder(
    private val passwordEncoder: PasswordEncoder
) {

    private companion object {
        val log = LoggingUtils(this)
    }

    fun encode(rawPassword: String): String =
        passwordEncoder.encode(rawPassword)
            ?: log.logAndThrow {
                PasswordEncoderException(
                    message = "An error occurred while processing password.",
                    internalMessage = "Password encoder returned null for non-null Raw Password given"
                )
            }

}