package com.yourRPG.chatPG.dto.ai.model

import com.yourRPG.chatPG.service.ai.providers.AiModel
import jakarta.validation.constraints.NotBlank

data class AiModelDto(

    @field:NotBlank
    val nickname: String?

) {

    constructor(aiModel: AiModel) : this(aiModel.nickname)

}