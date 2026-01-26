package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.domain.Chat
import com.yourRPG.chatPG.exception.chat.ForbiddenAccountException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AccountIsOwnerOfChatValidatorTest {

    @InjectMocks
    private lateinit var validator: AccountIsOwnerOfChatValidator

    private val chat = mock(Chat::class.java)

    @Test
    fun valid() {
        //ARRANGE
        val sameId = 1L
        val t = sameId to chat

        given(chat.ownerId)
            .willReturn(sameId)

        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(t)
        }
    }

    @Test
    fun invalid() {
        //ARRANGE
        val differentId = 2L
        val t = 0L to chat

        given(chat.ownerId)
            .willReturn(differentId)


        //ACT + ASSERT
        assertThrows<ForbiddenAccountException> {
            validator.validate(t)
        }
    }

    @Test
    fun `invalid - abnormal - ownerless chat`() {
        //ARRANGE
        val t = 0L to chat

        given(chat.ownerId)
            .willReturn(null)

        //ACT + ASSERT
        assertThrows<ForbiddenAccountException> {
            validator.validate(t)
        }
    }

}