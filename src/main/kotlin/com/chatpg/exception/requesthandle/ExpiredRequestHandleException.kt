package com.chatpg.exception.requesthandle

import com.fasterxml.jackson.annotation.JsonIgnore

class ExpiredRequestHandleException(
    message: String? = null,

    @field:JsonIgnore
    val accountId: Long
): IllegalStateException(message) {

    override fun toString(): String {
        return "ExpiredRequestHandleException: $message"
    }

}