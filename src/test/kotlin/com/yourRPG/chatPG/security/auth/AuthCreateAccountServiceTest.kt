package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.account.CreateAccountDto
import com.yourRPG.chatPG.exception.auth.password.*
import com.yourRPG.chatPG.exception.auth.username.BadUsernameException
import com.yourRPG.chatPG.exception.auth.username.UsernameContainsIllegalCharactersException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooLongException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooShortException
import com.yourRPG.chatPG.helper.email.MimeHelper
import com.yourRPG.chatPG.helper.frontend.FrontendUrlHelper
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.email.EmailService
import com.yourRPG.chatPG.validator.account.PasswordValidator
import com.yourRPG.chatPG.validator.account.UsernameValidator
import helper.NullSafeMatchers.STRING_TYPE
import helper.NullSafeMatchers.any
import helper.NullSafeMatchers.eq
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class AuthCreateAccountServiceTest {

    @InjectMocks
    private lateinit var service: AuthCreateAccountService

    @Mock private lateinit var passwordService: AuthPasswordService
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var emailService: EmailService

    @Mock private lateinit var usernameValidator: UsernameValidator
    @Mock private lateinit var passwordValidator: PasswordValidator

    @Mock private lateinit var mimeHelper: MimeHelper
    @Mock private lateinit var frontendUrlHelper: FrontendUrlHelper

    @Test
    fun `createAccount - success`() {
        //ARRANGE
        val username = "username_test"
        val email    = "email@email.com"
        val password = "Password-test"
        val dto = CreateAccountDto(username, email, password)
        val encryptedPassword = "encrypted-password-test"
        val account = Account(username, email, encryptedPassword)

        given(passwordService.encrypt(rawPassword = dto.password))
            .willReturn(encryptedPassword)

        given(accountService.saveAccountWith(dto.username, dto.email, encryptedPassword))
            .willReturn(account)

        //ACT
        service.createAccount(dto)

        //ASSERT
        verify(usernameValidator).validate(t = username)
        verify(passwordValidator).validate(t = password)

        verify(passwordService).encrypt(password)

        verify(accountService).saveAccountWith(username, email, encryptedPassword)

        verify(accountService).updateUuid(account.eq(), UUID.randomUUID().any())

        verify(emailService).sendMimeEmail(STRING_TYPE.any(), email.eq(), STRING_TYPE.any())

        verify(accountService).dtoOf(c = account)
    }

    @TestFactory
    fun `createAccount - thrown by UsernameValidator does propagate`(): Stream<DynamicTest> {
        //ARRANGE
        val username = "username_test"
        val email    = "email@email.com"
        val password = "Password-test"
        val dto = CreateAccountDto(username, email, password)

        return Stream.of(
            UsernameTooShortException("test"),
            UsernameTooLongException("test"),
            UsernameContainsIllegalCharactersException("test")
        ).map { exception ->
            DynamicTest.dynamicTest("Exception: $exception") {
                //ARRANGE
                given(usernameValidator.validate(t = username))
                    .willThrow(exception)

                //ACT + ASSERT
                assertThrows<BadUsernameException> {
                    service.createAccount(dto)
                }

                Mockito.reset(usernameValidator)
            }
        }
    }

    @TestFactory
    fun `createAccount - thrown by PasswordValidator does propagate`(): Stream<DynamicTest> {
        //ARRANGE
        val username = "username_test"
        val email    = "email@email.com"
        val password = "Password-test"
        val dto = CreateAccountDto(username, email, password)

        return Stream.of(
            PasswordTooShortException("test"),
            PasswordTooLongException("test"),
            PasswordDoesNotMeetCharactersOccurrenceCriteriaException("test"),
            PasswordContainsIllegalCharactersException("test")
        ).map { exception ->
            DynamicTest.dynamicTest("Exception: $exception") {
                //ARRANGE
                given(passwordValidator.validate(t = password))
                    .willThrow(exception)

                //ACT + ASSERT
                assertThrows<BadPasswordException> {
                    service.createAccount(dto)
                }

                Mockito.reset(passwordValidator)
            }
        }
    }

}