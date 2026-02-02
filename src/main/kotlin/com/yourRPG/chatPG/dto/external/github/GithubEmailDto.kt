package com.yourRPG.chatPG.dto.external.github

data class GithubEmailDto (

    val email: String?,

    val primary: Boolean = false,

    val verified: Boolean = false,

    val visibility: String?,

)
