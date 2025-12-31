package com.yourRPG.chatPG.dto.chat

import com.yourRPG.chatPG.model.Chat

data class ChatDto (
    val id: Long,
    val name: String
){

    constructor(chat: Chat): this(
        id   = chat.getId() ?: -1,
        name = chat.getName() ?: "!!! NO_NAME !!!"
    )

}