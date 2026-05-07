package com.chatpg.dto.auth

import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.UUID

data class FulfillPasswordChangeDto (

    @field:UUID
    val requestHandle: String,

    @field:Pattern(regexp = ".{8,255}")
    val password: String

)