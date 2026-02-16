package com.yourRPG.chatPG.dto.external.chutes

import com.fasterxml.jackson.annotation.JsonProperty

data class ChutesMetadata(
    @field:JsonProperty("weight_version") val weightVersion: String
) {

    constructor() : this(
        weightVersion = ""
    )

}