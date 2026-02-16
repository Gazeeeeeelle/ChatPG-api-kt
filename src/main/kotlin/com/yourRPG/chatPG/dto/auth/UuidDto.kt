package com.yourRPG.chatPG.dto.auth

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID

data class UuidDto(
    private val uuid: String,

    @field:JsonIgnore
    val value: UUID = UUID.fromString(uuid)
)
