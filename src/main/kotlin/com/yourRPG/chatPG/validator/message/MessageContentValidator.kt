package com.yourRPG.chatPG.validator.message

import com.yourRPG.chatPG.exception.message.MessageContentBlankException
import com.yourRPG.chatPG.exception.message.MessageContentNotFoundException
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class MessageContentValidator: IValidatable<String?> {

    /**
     * Assures that the nullable [String] given is not null and not blank
     *
     * @param [String] nullable
     * @return [String]
     * @throws MessageContentNotFoundException if the [String] was null
     * @throws MessageContentBlankException if the [String] was blank
     */
    override fun validate(t: String?): String {

        t ?: throw MessageContentNotFoundException("Message content cannot be null")

        t.trim().ifBlank { throw MessageContentBlankException("Message content cannot be blank") }

        return t
    }

}