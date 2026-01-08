package com.yourRPG.chatPG.dto.account

import jakarta.validation.constraints.Pattern


data class LoginCredentials(
    @field:Pattern(regexp = ".{3,255}")
    val username: String,
    @field:Pattern(regexp = ".{8,255}")
    val password: String
)