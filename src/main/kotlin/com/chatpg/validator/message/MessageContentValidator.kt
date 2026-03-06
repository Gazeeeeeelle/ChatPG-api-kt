package com.chatpg.validator.message

import com.chatpg.exception.message.MessageContentBlankException
import com.chatpg.exception.message.MessageContentNotFoundException
import com.chatpg.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class MessageContentValidator: IValidatable<String> {

    /**
     * Assures that the nullable [String] given is not null and not blank
     *
     * @param [String] nullable
     * @return [String]
     * @throws MessageContentNotFoundException if the [String] was null
     * @throws MessageContentBlankException if the [String] was blank
     */
    override fun validate(t: String) {

        if (t.isBlank()) {
            throw MessageContentBlankException("Message content cannot be blank")
        }

    }

}