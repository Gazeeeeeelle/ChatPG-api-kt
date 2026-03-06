package com.chatpg.validator.account

import com.chatpg.domain.account.Account
import com.chatpg.exception.auth.username.UsernameAlreadyRegisteredException
import com.chatpg.exception.email.EmailAlreadyRegisteredException
import com.chatpg.repository.AccountRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class AccountCreationCredentialsValidatorTest {

    @InjectMocks
    private lateinit var accountCreationCredentialsValidator: AccountCreationCredentialsValidator

    @Mock private lateinit var repository: AccountRepository

    @Test
    fun valid() {
        //ARRANGE
        val username = "username_test"
        val email    = "email@email.com"
        val password = "password_test"

        val account  = Account(username, email, password)

        //ACT + ASSERT
        assertDoesNotThrow { accountCreationCredentialsValidator.validate(account) }
    }

    @Test
    fun `invalid - username already registered`() {
        //ARRANGE
        val username = "username_test"
        val email    = "email@email.com"
        val password = "password_test"

        val account = Account(username, email, password)

        given(repository.existsByNameEquals(username))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<UsernameAlreadyRegisteredException> {
            accountCreationCredentialsValidator.validate(account)
        }

    }

    @Test
    fun `invalid - email already registered`() {
        //ARRANGE
        val username = "username_test"
        val email    = "email@email.com"
        val password = "password_test"

        val account = Account(username, email, password)

        given(repository.qExistsByEmail(email))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<EmailAlreadyRegisteredException> {
            accountCreationCredentialsValidator.validate(account)
        }

    }

}