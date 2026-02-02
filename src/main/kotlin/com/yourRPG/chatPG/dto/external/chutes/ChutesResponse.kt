package com.yourRPG.chatPG.dto.external.chutes

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesResponse(
    val id: String,
    @field: JsonProperty("object") val obj: String,
    val created: Long,
    val model: String,
    val choices: List<ChutesChoice>,
    val usage: ChutesUsage,
    val metadata: ChutesMetadata,
    @field: JsonProperty("chutes_verification") val chutesVerification: String
)
