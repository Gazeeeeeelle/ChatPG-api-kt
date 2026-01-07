package com.yourRPG.chatPG.validator.message

import com.yourRPG.chatPG.exception.message.MessageContentBlankException
import com.yourRPG.chatPG.exception.message.MessageContentNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MessageContentValidatorTest {

    val validator = MessageContentValidator()

    @Test
    fun valid() {
        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(t = "hi")
        }
    }

    @Test
    fun invalid_null() {
        //ACT + ASSERT
        assertThrows<MessageContentNotFoundException> {
            validator.validate(t = null)
        }
    }

    @Test
    fun invalid_blank() {
        //ACT + ASSERT
        assertThrows<MessageContentBlankException> {
            validator.validate(t = "")
        }
    }

}