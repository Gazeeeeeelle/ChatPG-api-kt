package com.yourRPG.chatPG.dto.chat

import com.yourRPG.chatPG.dto.account.AccountDto

data class AccountChatDto(
    val accountDto: AccountDto,
    val chatDto: ChatDto
)
