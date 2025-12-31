package com.yourRPG.chatPG.dto.chat

import com.yourRPG.chatPG.dto.account.AccountDto

data class GetChatDto(
    val accountDto: AccountDto,
    val chat: String
)
