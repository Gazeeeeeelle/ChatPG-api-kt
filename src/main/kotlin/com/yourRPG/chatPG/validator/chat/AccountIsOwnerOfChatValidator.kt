package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.exception.chat.ForbiddenAccountException
import com.yourRPG.chatPG.domain.Chat
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class AccountIsOwnerOfChatValidator: IValidatable<Pair<Long, Chat>> {

    override fun validate(t: Pair<Long, Chat>) {
        val (accountId, chat) = t

        if (accountId != chat.ownerId)
            throw ForbiddenAccountException("This account does not attend to the requirements to realize such action")

    }

}