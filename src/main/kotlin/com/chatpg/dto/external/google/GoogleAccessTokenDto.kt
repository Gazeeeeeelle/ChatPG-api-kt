package com.chatpg.dto.external.google

import com.fasterxml.jackson.annotation.JsonProperty
import com.chatpg.dto.external.google.error.GoogleErrorDto

data class GoogleAccessTokenDto (

    @field:JsonProperty("access_token")
    val accessToken: String?,

    @field:JsonProperty("expires_in")
    val expiresIn: Long?,

    val scope: String?,

    @field:JsonProperty("token_type")
    val tokenType: String?,

    @field:JsonProperty("id_token")
    val idToken: String?,

    val error: GoogleErrorDto?
)
