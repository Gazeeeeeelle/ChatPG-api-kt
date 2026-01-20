package com.yourRPG.chatPG.dto.auth

import org.hibernate.validator.constraints.UUID

data class UuidDto(

    @field: UUID(message = "Invalid link")
    val uuid: String

)
