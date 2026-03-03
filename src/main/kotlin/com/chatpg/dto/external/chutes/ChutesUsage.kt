package com.chatpg.dto.external.chutes

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesUsage (
    @field:JsonProperty("prompt_tokens") val promptTokens: Long,
    @field:JsonProperty("total_tokens") val totalTokens: Long,
    @field:JsonProperty("completion_tokens") val completionTokens: Long,
    @field:JsonProperty("prompt_tokens_details") val promptTokensDetails: ChutesPromptTokenDetails,
    @field:JsonProperty("reasoning_tokens") val reasoningTokens: Long
)