package com.yourRPG.chatPG.service.ai.providers.chutes.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesUsage (
    @field:JsonProperty("prompt_tokens") val promptTokens: Long,
    @field:JsonProperty("total_tokens") val totalTokens: Long,
    @field:JsonProperty("completion_tokens") val completionTokens: Long,
    @field:JsonProperty("prompt_tokens_details") val promptTokensDetails: ChutesPromptTokenDetails,
    @field:JsonProperty("reasoning_tokens") val reasoningTokens: Long
) {
    constructor() : this(
        promptTokens = -1L,
        totalTokens = -1L,
        completionTokens = -1L,
        promptTokensDetails = ChutesPromptTokenDetails(),
        reasoningTokens = -1L
    )
}