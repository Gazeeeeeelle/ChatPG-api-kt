package com.yourRPG.chatPG.dto.chat

import com.yourRPG.chatPG.dto.account.AccountDto

data class ChooseModelDto(
    val accountDto: AccountDto,
    val chatDto: ChatDto,
    val model: String
)
