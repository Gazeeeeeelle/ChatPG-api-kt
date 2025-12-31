package com.yourRPG.chatPG.dto.message

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.chat.ChatDto

data class SendMessageDto(
    val accountDto: AccountDto,
    val chatDto: ChatDto,
    val content: String
)
