package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.account.CreateAccountDto
import com.yourRPG.chatPG.exception.ConflictException
import com.yourRPG.chatPG.exception.auth.AccountActivationException
import com.yourRPG.chatPG.exception.auth.password.*
import com.yourRPG.chatPG.exception.auth.username.BadUsernameException
import com.yourRPG.chatPG.exception.auth.username.UsernameContainsIllegalCharactersException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooLongException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooShortException
import com.yourRPG.chatPG.helper.email.MimeHelper
import com.yourRPG.chatPG.helper.frontend.FrontendUrlHelper
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.account.AccountStatus
import com.yourRPG.chatPG.service.email.EmailService
import com.yourRPG.chatPG.validator.account.PasswordValidator
import com.yourRPG.chatPG.validator.account.UsernameValidator
import helper.NullSafeMatchers.LONG_TYPE
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
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.time.temporal.ChronoUnit
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
        val encryptedPassword = "encrypted-password-test"

        val dto = CreateAccountDto(username, email, password)
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

        verify(passwordService)
            .encrypt(password)

        verify(accountService)
            .saveAccountWith(username, email, encryptedPassword)

        verify(accountService)
            .updateUuid(account.eq(), UUID.randomUUID().any())

        verify(emailService)
            .sendMimeEmail(STRING_TYPE.any(), email.eq(), STRING_TYPE.any())

        verify(accountService)
            .dtoOf(c = account)
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

                reset(usernameValidator)
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

                reset(passwordValidator)
            }
        }
    }

    @Test
    fun `activateAccount - success`() {
        //ARRANGE
        val dto = UuidDto("b22ef8ea-bd27-48fe-994c-568e6a4e58e4")
        val uuid = UUID.fromString(dto.uuid)
        val account = mock(Account::class.java)

        given(accountService.getByUuid(uuid))
            .willReturn(account)

        given(account.status)
            .willReturn(AccountStatus.DISABLED)

        given(account.uuidBirth)
            .willReturn(Instant.now())

        //ACT
        service.activateAccount(dto)

        //ASSERT
        verify(accountService)
            .updateUuid(account, null)

        verify(accountService)
            .updateStatus(account, AccountStatus.ENABLED)

        verify(accountService, never())
            .deleteById(LONG_TYPE.any())
    }

    @TestFactory
    fun `activateAccount - failure - abnormal - account is not disabled`(): Stream<DynamicTest> {
        //ARRANGE
        val dto = UuidDto("b22ef8ea-bd27-48fe-994c-568e6a4e58e4")
        val uuid = UUID.fromString(dto.uuid)
        val account = mock(Account::class.java)

        return Stream.of(
            AccountStatus.ENABLED,
            AccountStatus.DELETED,
        ).map { status ->
            DynamicTest.dynamicTest("Status: $status") {
                given(accountService.getByUuid(uuid))
                    .willReturn(account)

                given(account.status)
                    .willReturn(status)

                //ACT + ASSERT
                assertThrows<ConflictException> {
                    service.activateAccount(dto)
                }

                verify(accountService)
                    .updateUuid(account, uuid = null)

                verify(accountService, never())
                    .updateStatus(account.eq(), status = AccountStatus.entries.random().any())

                verify(accountService, never())
                    .deleteById(LONG_TYPE.any())

                reset(accountService)
            }
        }
    }

    @Test
    fun `activateAccount - failure - abnormal - absence of uuid birth`() {
        //ARRANGE
        val dto = UuidDto("b22ef8ea-bd27-48fe-994c-568e6a4e58e4")
        val uuid = UUID.fromString(dto.uuid)
        val account = mock(Account::class.java)

        given(accountService.getByUuid(uuid))
            .willReturn(account)

        given(account.status)
            .willReturn(AccountStatus.DISABLED)

        //ACT + ASSERT
        assertThrows<AccountActivationException> {
            service.activateAccount(dto)
        }

        //ASSERT
        verify(accountService)
            .updateUuid(account, null)

        verify(accountService, never())
            .updateStatus(account.eq(), AccountStatus.entries.random().any())

        verify(accountService, never())
            .deleteById(LONG_TYPE.any())

    }


    @Test
    fun `activateAccount - failure - expired`() {
        //ARRANGE
        val dto = UuidDto("b22ef8ea-bd27-48fe-994c-568e6a4e58e4")
        val uuid = UUID.fromString(dto.uuid)
        val account = mock(Account::class.java)

        given(accountService.getByUuid(uuid))
            .willReturn(account)

        given(account.status)
            .willReturn(AccountStatus.DISABLED)

        val thirtyMinutesAgo = Instant.now()
            .minus(30, ChronoUnit.MINUTES)

        given(account.uuidBirth)
            .willReturn(thirtyMinutesAgo)

        //ACT + ASSERT
        assertThrows<AccountActivationException> {
            service.activateAccount(dto)
        }

        //ASSERT
        verify(accountService)
            .updateUuid(account, null)

        verify(accountService)
            .deleteById(LONG_TYPE.any())

    }

}