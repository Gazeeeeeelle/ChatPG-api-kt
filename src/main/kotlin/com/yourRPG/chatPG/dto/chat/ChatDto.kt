package com.yourRPG.chatPG.dto.chat

import com.yourRPG.chatPG.model.Chat
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class ChatDto (

    @field: NotNull
    val id: Long?,

    @field:NotBlank
    val name: String?

){

    constructor(chat: Chat): this(
        id   = chat.id,
        name = chat.name
    )

}