package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.exception.chat.UnauthorizedAccountException
import com.yourRPG.chatPG.domain.Chat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AccountIsOwnerOfChatValidatorTest {

    @InjectMocks
    lateinit var validator: AccountIsOwnerOfChatValidator

    val chat: Chat = Mockito.mock(Chat::class.java)

    val t: Pair<Long, Chat> = Pair(0, chat)

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
        assertThrows<UnauthorizedAccountException> {
            validator.validate(t)
        }
    }

}