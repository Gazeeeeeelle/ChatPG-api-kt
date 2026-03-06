package com.chatpg.validator.message

import com.chatpg.exception.message.MessageContentBlankException
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
    fun invalid_empty() {
        //ACT + ASSERT
        assertThrows<MessageContentBlankException> {
            validator.validate(t = "")
        }
    }

    @Test
    fun invalid_blank() {
        //ACT + ASSERT
        assertThrows<MessageContentBlankException> {
            validator.validate(t = "   ")
        }
    }

}