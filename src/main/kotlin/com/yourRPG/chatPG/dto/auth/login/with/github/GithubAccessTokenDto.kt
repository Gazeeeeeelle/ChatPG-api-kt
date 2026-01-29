package com.yourRPG.chatPG.dto.auth.login.with.github

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped

data class GithubAccessTokenDto(

    @field:JsonProperty("access_token")
    val accessToken: String?,

    @field:JsonUnwrapped
    val errorDto: GithubErrorDto

)
