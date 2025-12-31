package com.yourRPG.chatPG.service.ai.providers.chutes.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesMetadata(
    @field:JsonProperty("weight_version") val weightVersion: String
) {

    constructor() : this(
        weightVersion = ""
    )

}