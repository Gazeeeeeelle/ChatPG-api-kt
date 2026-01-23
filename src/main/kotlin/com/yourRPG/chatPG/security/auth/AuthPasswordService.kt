package com.yourRPG.chatPG.security.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthPasswordService(
    val passwordEncoder: PasswordEncoder
) {

    fun encrypt(rawPassword: String): String = passwordEncoder.encode(rawPassword)

}