package com.yourRPG.chatPG.dto.auth

import jakarta.validation.constraints.NotBlank

data class LoginCredentials(

    @param:NotBlank
    val username: String,

    @param:NotBlank
    val password: String

)