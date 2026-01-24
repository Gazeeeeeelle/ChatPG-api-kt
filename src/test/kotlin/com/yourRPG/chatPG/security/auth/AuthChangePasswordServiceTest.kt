package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.account.ChangePasswordDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.auth.password.PasswordResetException
import com.yourRPG.chatPG.helper.email.MimeHelper
import com.yourRPG.chatPG.helper.frontend.FrontendUrlHelper
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.email.EmailService
import helper.NullSafeMatchers.STRING_TYPE
import helper.NullSafeMatchers.any
import helper.NullSafeMatchers.eq
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class AuthChangePasswordServiceTest {

    @InjectMocks
    private lateinit var service: AuthChangePasswordService

    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var mimeHelper: MimeHelper
    @Mock private lateinit var frontendUrlHelper: FrontendUrlHelper
    @Mock private lateinit var emailService: EmailService
    @Mock private lateinit var passwordService: AuthPasswordService

    @Test
    fun `requestChangePassword - success`() {
        //ARRANGE
        val email   = "email@email.com"
        val account = Account("username_test", email, "encrypted-password-test")

        val html    = "html-test"
        val url     = "url-test"

        given(accountService.getByEmail(email))
            .willReturn(account)

        given(frontendUrlHelper.append(STRING_TYPE.any()))
            .willReturn(url)

        given(mimeHelper.getTemplate(STRING_TYPE.any(), ("url" to url).eq()))
            .willReturn(html)

        //ACT
        service.requestChangePassword(email)

        //ASSERT
        verify(accountService)
            .updateUuid(account = account.eq(), any(UUID::class.java))

        verify(emailService)
            .sendMimeEmail(subject = "Reset password", to = email, html)
    }

    @Test
    fun `requestChangePassword - account not found with email given`() {
        //ARRANGE
        val email = "email@email.com"
        given(accountService.getByEmail(email))
            .willThrow(AccountNotFoundException("Account not found with email $email"))

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.requestChangePassword(email)
        }
    }

    @Test
    fun `confirmChangePassword - success`() {
        //ARRANGE
        val uuidString = "1ffa5d78-ab2d-e3de-ae5d-ab1fdabcfdde"
        val uuid = UUID.fromString(uuidString)

        val username          = "username_test"
        val email             = "email@email.com"
        val rawPassword       = "password-test"
        val encryptedPassword = "encrypted-password-test"

        val changePassword = ChangePasswordDto(uuidString, rawPassword)
        val account = Account(username, email, encryptedPassword)

        given(accountService.getByUuid(uuid))
            .willReturn(account)

        account.uuidBirth = Instant.now()

        given(passwordService.encrypt(rawPassword))
            .willReturn(encryptedPassword)

        //ACT
        service.confirmChangePassword(changePassword)

        //ASSERT
        verify(accountService).apply {
            updateUuid(account, null)
            updatePassword(account, encryptedPassword)
        }

    }

    @Test
    fun `confirmChangePassword - expired uuid`() {
        //ARRANGE
        val uuidString = "1ffa5d78-ab2d-e3de-ae5d-ab1fdabcfdde"
        val uuid = UUID.fromString(uuidString)

        val username          = "username_test"
        val email             = "email@email.com"
        val rawPassword       = "password-test"
        val encryptedPassword = "encrypted-password-test"

        val changePassword = ChangePasswordDto(uuidString, rawPassword)
        val account = Account(username, email, encryptedPassword)

        given(accountService.getByUuid(uuid))
            .willReturn(account)

        val twentyMinutes = Duration.ofMinutes(15)

        account.uuidBirth = Instant.now().minus(twentyMinutes)

        //ACT
        service.confirmChangePassword(changePassword)

        //ASSERT
        verify(accountService).
            updateUuid(account, null)
        verify(accountService, never())
            .updatePassword(account, encryptedPassword)
    }

    @Test
    fun `confirmChangePassword - account not found with uuid given`() {
        //ARRANGE
        val uuidString = "1ffa5d78-ab2d-e3de-ae5d-ab1fdabcfdde"
        val uuid = UUID.fromString(uuidString)

        val rawPassword = "password-test"
        val changePassword = ChangePasswordDto(uuidString, rawPassword)

        given(accountService.getByUuid(uuid))
            .willThrow(AccountNotFoundException("No account found with uuid given"))

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.confirmChangePassword(changePassword)
        }

    }

    @Test
    fun `confirmChangePassword - abnormal absence of uuid birth`() {
        //ARRANGE
        val uuidString  = "1ffa5d78-ab2d-e3de-ae5d-ab1fdabcfdde"
        val uuid        = UUID.fromString(uuidString)

        val username    = "username_test"
        val email       = "email@email.com"
        val rawPassword = "password-test"

        val changePassword = ChangePasswordDto(uuidString, rawPassword)
        val account = Account(username, email, "encrypted-password-test")

        given(accountService.getByUuid(uuid))
            .willReturn(account)

        //ACT + ASSERT
        assertThrows<PasswordResetException> {
            service.confirmChangePassword(changePassword)
        }

        verify(accountService)
            .updateUuid(account, null)

    }

}