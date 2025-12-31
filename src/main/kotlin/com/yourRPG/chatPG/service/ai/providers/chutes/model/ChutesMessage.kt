package com.yourRPG.chatPG.service.ai.providers.chutes.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesMessage(
    val role: String,
    val content: String,
    @field:JsonProperty("reasoning_content") val reasoningContent: String,
    @field:JsonProperty("tool_call") val toolCall: String
) {

    constructor() : this(
        role = "",
        content = "",
        reasoningContent = "",
        toolCall = ""
    )

}
