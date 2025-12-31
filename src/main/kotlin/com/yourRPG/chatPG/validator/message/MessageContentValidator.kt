package com.yourRPG.chatPG.validator.message

import com.yourRPG.chatPG.exception.message.BlankMessageContentException
import com.yourRPG.chatPG.exception.message.MessageContentNotFoundException
import com.yourRPG.chatPG.validator.IValidatable

class MessageContentValidator: IValidatable<String?> {

    /**
     * TODO
     */
    override fun validate(t: String?): String {
        t ?: throw MessageContentNotFoundException("Message content cannot be null")

        t.ifBlank { throw BlankMessageContentException("Message content cannot be blank") }

        return t
    }

}