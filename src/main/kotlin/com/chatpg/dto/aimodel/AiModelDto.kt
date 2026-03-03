package com.chatpg.dto.aimodel

import jakarta.validation.constraints.NotBlank

data class AiModelDto(

    @field:NotBlank
    val nickname: String?

)