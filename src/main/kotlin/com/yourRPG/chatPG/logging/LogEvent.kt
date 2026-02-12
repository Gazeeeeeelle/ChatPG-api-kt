package com.yourRPG.chatPG.logging

import com.fasterxml.jackson.annotation.JsonProperty

data class LogEvent(
    val timestamp: String,

    val level: String,

    val message: String,

    @field:JsonProperty("exception_type")
    val exceptionType: String,

    val reason: String,

    @field:JsonProperty("user_id")
    val userId: String,

    val path: String
)
