package com.chatpg.dto.chat

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.util.*

data class ChatDto (

    @field:NotNull
    val publicId: UUID?,

    @field:NotBlank
    val name: String?

)
