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
        id      = message.id ?: -1,
        content = message.content,
        bot     = message.bot,
        account =
            if (message.account != null) {
                AccountDto(message.account!!)
            } else {
                null
            }
    )

}
