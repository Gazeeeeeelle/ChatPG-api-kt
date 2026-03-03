package com.chatpg.dto.auth

import jakarta.validation.constraints.Email

data class OpenPasswordChangeDto(
    @param:Email
    val email: String
)