package com.yourRPG.chatPG.dto.auth

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class FulfillA2FRequestDto(

    val requestHandle: UUID,

    @param:NotBlank
    val code: String

)
