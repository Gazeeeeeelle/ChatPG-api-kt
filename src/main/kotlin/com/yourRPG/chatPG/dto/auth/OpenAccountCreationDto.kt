package com.yourRPG.chatPG.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class OpenAccountCreationDto(

    @field:NotBlank
    val username: String,

    @field:Email(message = "Please, enter a valid email")
    val email: String,

    @field:NotBlank
    val password: String

)