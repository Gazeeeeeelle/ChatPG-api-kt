package com.yourRPG.chatPG.dto.auth.login.with.github

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubErrorDto(

    val error: String?,

    @param:JsonProperty("error_description")
    val errorDescription: String?,

    @param:JsonProperty("error_uri")
    val errorUri: String?,

) {

    val present = error != null || errorDescription != null

}
