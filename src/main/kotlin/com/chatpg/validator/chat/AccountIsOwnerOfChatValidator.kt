package com.chatpg.validator.chat

import com.chatpg.exception.chat.ForbiddenAccountException
import com.chatpg.domain.chat.Chat
import com.chatpg.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class AccountIsOwnerOfChatValidator: IValidatable<Pair<Long, Chat>> {

    override fun validate(t: Pair<Long, Chat>) {
        val (accountId, chat) = t

        if (accountId != chat.ownerId)
            throw ForbiddenAccountException("This account does not attend to the requirements to realize such action")

    }

}