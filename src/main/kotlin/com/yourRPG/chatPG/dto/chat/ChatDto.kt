package com.yourRPG.chatPG.dto.chat

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class ChatDto (

    @field:NotNull
    val id: Long?,

    @field:NotBlank
    val name: String?

)
