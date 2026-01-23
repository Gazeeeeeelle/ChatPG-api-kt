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

    private val t: Pair<Long, Chat> = Pair(0, chat)

    @Test
    fun valid() {
        //ARRANGE
        given(t.second.ownerId)
            .willReturn(t.first)

        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(t)
        }
    }

    @Test
    fun invalid() {
        //ARRANGE
        given(t.second.ownerId)
            .willReturn(t.first + 1)

        //ACT + ASSERT
        assertThrows<ForbiddenAccountException> {
            validator.validate(t)
        }
    }

}