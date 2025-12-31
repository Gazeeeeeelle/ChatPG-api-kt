package com.yourRPG.chatPG.service.ai.providers.chutes.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesPromptTokenDetails(
    @field:JsonProperty("cached_tokens") val cachedTokens: Long,
    @field:JsonProperty("reasoning_tokens") val reasoningTokens: Long
) {

    constructor() : this(
        cachedTokens = -1L,
        reasoningTokens = -1L
    )

}