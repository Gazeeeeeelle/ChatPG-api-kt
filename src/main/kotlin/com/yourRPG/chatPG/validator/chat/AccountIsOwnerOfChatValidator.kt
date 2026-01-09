package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.exception.chat.UnauthorizedAccountException
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class AccountIsOwnerOfChatValidator: IValidatable<Pair<Long, Chat>> {

    override fun validate(t: Pair<Long, Chat>): Pair<Long, Chat> {
        val (accountId, chat) = t

        if (accountId != chat.ownerId)
            throw UnauthorizedAccountException("This account does not attend to the requirements to realize such action")

        return t
    }

}