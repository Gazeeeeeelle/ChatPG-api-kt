package com.yourRPG.chatPG.dto.auth

import jakarta.validation.constraints.Pattern

data class ChangePasswordDto (

    @field:Pattern(
        regexp = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "Invalid UUID format"
    )
    val uuid: String,

    @field:Pattern(regexp = ".{8,255}")
    val password: String

)