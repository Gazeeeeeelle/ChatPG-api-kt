package com.yourRPG.chatPG.dto.external.google.error

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleErrorDetailsDto (

    @field:JsonProperty("@type")
    val type: String,

    val fieldViolations: List<GoogleErrorDetailsFieldViolation>

)