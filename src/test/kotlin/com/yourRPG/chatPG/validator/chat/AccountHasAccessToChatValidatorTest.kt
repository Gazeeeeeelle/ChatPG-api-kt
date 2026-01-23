package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.exception.chat.UnauthorizedAccessToChatException
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.repository.ChatRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AccountHasAccessToChatValidatorTest {

    @InjectMocks
    lateinit var validator: AccountHasAccessToChatValidator

    @Mock lateinit var accountRepository: AccountRepository
    @Mock lateinit var chatRepository: ChatRepository

    var t: Pair<Long, Long> = Pair(0, 0)

    @Test
    fun valid() {
        //ARRANGE
        given(accountRepository.existsById(t.first))
            .willReturn(true)

        given(chatRepository.existsById(t.second))
            .willReturn(true)

        given(chatRepository.qExistsByAccountNameAndId(t.first, t.second))
            .willReturn(true)

        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(t)
        }
    }

    @Test
    fun invalid_accountDoesNotExist() {
        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            validator.validate(t)
        }
    }

    @Test
    fun invalid_chatDoesNotExist() {
        //ARRANGE
        given(accountRepository.existsById(t.first))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<ChatNotFoundException> {
            validator.validate(t)
        }
    }

    @Test
    fun invalid_doesNotHaveAccessToChat() {
        //ARRANGE
        given(accountRepository.existsById(t.first))
            .willReturn(true)

        given(chatRepository.existsById(t.second))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<UnauthorizedAccessToChatException> {
            validator.validate(t)
        }
    }

}