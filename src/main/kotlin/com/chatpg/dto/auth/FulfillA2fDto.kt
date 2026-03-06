package com.chatpg.dto.auth

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class FulfillA2fDto(

    val requestHandle: UUID,

    @param:NotBlank
    val code: String

)
