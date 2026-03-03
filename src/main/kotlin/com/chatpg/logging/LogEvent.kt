package com.chatpg.logging

import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.event.Level
import java.time.Instant

data class LogEvent(
    val timestamp: Instant,

    val level: Level,

    val message: String,

    @field:JsonProperty("exception_type")
    val exceptionType: String,

    val reason: String,

    @field:JsonProperty("user_id")
    val userId: String,

    val path: String
)
