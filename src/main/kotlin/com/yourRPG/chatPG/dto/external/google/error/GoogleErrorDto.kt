package com.yourRPG.chatPG.dto.external.google.error

data class GoogleErrorDto (

    val code: Int,

    val message: String,

    val status: String,

    val details: List<GoogleErrorDetailsDto>

)