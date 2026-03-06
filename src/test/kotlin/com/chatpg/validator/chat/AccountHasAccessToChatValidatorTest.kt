package com.chatpg.validator.chat

import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.chat.ChatNotFoundException
import com.chatpg.exception.chat.ForbiddenAccessToChatException
import com.chatpg.repository.AccountRepository
import com.chatpg.repository.ChatRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AccountHasAccessToChatValidatorTest {

    @InjectMocks
    lateinit var validator: AccountHasAccessToChatValidator

    @Mock lateinit var accountRepository: AccountRepository
    @Mock lateinit var chatRepository: ChatRepository

    @Test
    fun valid() {
        //ARRANGE
        val uuid = UUID.randomUUID()
        val accountId = 0L

        given(accountRepository.existsById(accountId))
            .willReturn(true)

        given(chatRepository.qExistsByPublicId(uuid))
            .willReturn(true)

        given(chatRepository.qExistsByAccountNameAndId(accountId, uuid))
            .willReturn(true)

        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(accountId to uuid)
        }
    }

    @Test
    fun invalid_accountDoesNotExist() {
        //ACT + ASSERT
        val uuid = UUID.randomUUID()
        val accountId = 0L

        assertThrows<AccountNotFoundException> {
            validator.validate(accountId to uuid)
        }
    }

    @Test
    fun invalid_chatDoesNotExist() {
        //ARRANGE
        val accountId = 0L
        val uuid = UUID.randomUUID()

        given(accountRepository.existsById(accountId))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<ChatNotFoundException> {
            validator.validate(accountId to uuid)
        }
    }

    @Test
    fun invalid_doesNotHaveAccessToChat() {
        //ARRANGE
        val accountId = 0L
        val uuid = UUID.randomUUID()

        given(accountRepository.existsById(accountId))
            .willReturn(true)

        given(chatRepository.qExistsByPublicId(uuid))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<ForbiddenAccessToChatException> {
            validator.validate(accountId to uuid)
        }
    }

}