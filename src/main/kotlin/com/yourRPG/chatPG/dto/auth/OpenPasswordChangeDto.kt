package com.yourRPG.chatPG.dto.auth

import jakarta.validation.constraints.Email

data class OpenPasswordChangeDto(
    @param:Email
    val email: String
)