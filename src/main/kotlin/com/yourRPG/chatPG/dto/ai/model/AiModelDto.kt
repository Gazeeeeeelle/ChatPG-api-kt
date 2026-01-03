package com.yourRPG.chatPG.dto.ai.model

import com.yourRPG.chatPG.service.ai.providers.AiModel

data class AiModelDto(
    val nickname: String
) {

    constructor(aiModel: AiModel) : this(aiModel.getNickName())

}