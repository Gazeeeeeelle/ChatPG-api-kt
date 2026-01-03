package com.yourRPG.chatPG.dto.message

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.model.Message

data class MessageDto(
    val id: Long,
    val content: String,
    val bot: Boolean,
    val account: AccountDto?
) {

    constructor(message: Message): this(
        id      = message.getId() ?: -1,
        content = message.getContent(),
        bot     = message.isBot(),
        account =
            if (message.getAccount() != null) {
                AccountDto(message.getAccount()!!)
            } else {
                null
            }
    )

}
