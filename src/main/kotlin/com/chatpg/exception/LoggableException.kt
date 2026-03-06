package com.chatpg.exception

import com.fasterxml.jackson.annotation.JsonIgnore
import org.slf4j.event.Level

abstract class LoggableException(
    @field:JsonIgnore
    val level: Level,

    message: String,

    @field:JsonIgnore
    val internalMessage: String = message
): RuntimeException(message)