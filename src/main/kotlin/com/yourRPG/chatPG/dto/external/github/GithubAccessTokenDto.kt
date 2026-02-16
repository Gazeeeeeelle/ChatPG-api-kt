package com.yourRPG.chatPG.dto.external.github

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.yourRPG.chatPG.dto.external.github.error.GithubErrorDto

data class GithubAccessTokenDto(

    @field:JsonProperty("access_token")
    val accessToken: String?,

    @field:JsonUnwrapped
    val errorDto: GithubErrorDto

)
