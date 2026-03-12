package com.chatpg.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UuidDto(
    @field:JsonProperty("uuid")
    private val uuidString: String,
) {
    val uuid: UUID = UUID.fromString(uuidString)
}
