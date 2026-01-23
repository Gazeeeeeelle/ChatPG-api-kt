package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.exception.auth.username.UsernameAlreadyRegisteredException
import com.yourRPG.chatPG.exception.email.EmailAlreadyRegisteredException
import com.yourRPG.chatPG.repository.AccountRepository
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
        val t = "username" to "email@email.com"

        //ACT + ASSERT
        assertDoesNotThrow { accountCreationCredentialsValidator.validate(t) }
    }

    @Test
    fun `invalid - username already registered`() {
        //ARRANGE
        val username = "username"
        val email = "email@email.com"

        val t = username to email

        given(repository.existsByNameEquals(username))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<UsernameAlreadyRegisteredException> {
            accountCreationCredentialsValidator.validate(t)
        }

    }

    @Test
    fun `invalid - email already registered`() {
        //ARRANGE
        val username = "username"
        val email = "email@email.com"

        val t = username to email

        given(repository.qExistsByEmail(email))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<EmailAlreadyRegisteredException> {
            accountCreationCredentialsValidator.validate(t)
        }

    }

}