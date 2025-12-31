package com.yourRPG.chatPG.service.ai.providers.chutes.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesChoice(
    val index: Long,
    val message: ChutesMessage,
    @field: JsonProperty("logprobs") val logProbabilities: String,
    @field: JsonProperty("finish_reason") val finishReason: String,
    @field: JsonProperty("matched_stop") val matchedStop: Long
) {

    constructor() : this(
        index = -1L,
        message = ChutesMessage(),
        logProbabilities = "",
        finishReason = "",
        matchedStop = -1
    )

}
