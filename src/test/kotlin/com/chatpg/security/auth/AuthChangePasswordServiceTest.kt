package com.chatpg.security.auth

import com.chatpg.domain.account.Account
import com.chatpg.dto.auth.FulfillPasswordChangeDto
import com.chatpg.dto.auth.OpenPasswordChangeDto
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.auth.password.BadPasswordException
import com.chatpg.infra.email.EmailService
import com.chatpg.infra.uri.FrontendUriHelper
import com.chatpg.security.helper.NullSafePasswordEncoder
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.security.requesthandle.RequestHandleSubject
import com.chatpg.service.account.AccountService
import com.chatpg.validator.account.PasswordValidator
import helper.NullSafeMatchers.STRING_TYPE
import helper.NullSafeMatchers.any
import helper.NullSafeMatchers.that
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.util.*

@ExtendWith(MockitoExtension::class)
class AuthChangePasswordServiceTest {

    private lateinit var service: AuthChangePasswordService

    @Mock private lateinit var passwordEncoder: NullSafePasswordEncoder
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var emailService: EmailService
    @Mock private lateinit var requestHandleService: RequestHandleService
    @Mock private lateinit var frontendUriHelper: FrontendUriHelper
    @Mock private lateinit var passwordValidator: PasswordValidator

    private val changePasswordExpiresIn = Duration.ofMinutes(10L)

    @BeforeEach
    fun setUp() {
        service = AuthChangePasswordService(
            passwordEncoder,
            accountService,
            emailService,
            requestHandleService,
            frontendUriHelper,
            changePasswordExpiresIn,
            passwordValidator,
        )
    }

    @Test
    fun `openPasswordChange - success`() {
        //ARRANGE
        val email = "email@email.com"
        val dto = OpenPasswordChangeDto(email)
        val account = Account("username_test", "email@email.com", "Password-test")
        val subject = RequestHandleSubject.CHANGE_PASSWORD
        val uuid = UUID.randomUUID()
        val url = "url/containing/$uuid"

        given(accountService.getByEmail(email))
            .willReturn(account)

        given(requestHandleService.newRequestHandle(account, subject))
            .willReturn(uuid)

        given(frontendUriHelper.appendString(STRING_TYPE.any()))
            .willReturn(url)

        //ACT
        service.openPasswordChange(dto)

        //ASSERT
        verify(accountService)
            .getByEmail(email)

        verify(requestHandleService)
            .newRequestHandle(account, subject)

        verify(frontendUriHelper)
            .appendString(STRING_TYPE.that { it.contains(uuid.toString()) })

        verify(emailService)
            .sendMimeEmailWithTemplate(
                "Reset password",
                email,
                "mime-change-password",
                "url" to url
            )
    }

    @Test
    fun `openPasswordChange - failure - account not found`() {
        //ARRANGE
        val email = "email@email.com"
        val dto = OpenPasswordChangeDto(email)

        given(accountService.getByEmail(email))
            .willThrow(AccountNotFoundException::class.java)

        //ACT
        assertDoesNotThrow {
            service.openPasswordChange(dto)
        }

        verify(emailService, never())
            .sendMimeEmail(
                subject = STRING_TYPE.any(),
                to      = STRING_TYPE.any(),
                html    = STRING_TYPE.any()
            )
    }

    @Test
    fun `fulfillPasswordChange - success`() {
        //ARRANGE
        val requestHandleString = "019c477c-6d06-70a8-bcc5-eab25612b96c"
        val requestHandle = UUID.fromString(requestHandleString)
        val password = "Password-test"
        val encodedPassword = "EncodedPassword-test"

        val dto = FulfillPasswordChangeDto(requestHandleString, password)

        val account = Account("username_test", "email@email.com", "Password-test")

        given(requestHandleService.getAccountAndDiscardCheckedHandle(
                uuid = requestHandle,
                subject = RequestHandleSubject.CHANGE_PASSWORD,
                expirationTime = changePasswordExpiresIn
        )).willReturn(account)

        given(passwordEncoder.encode(password))
            .willReturn(encodedPassword)

        //ACT
        service.fulfillPasswordChange(dto)

        //ASSERT
        verify(passwordValidator)
            .validate(password)

        verify(accountService)
            .updatePassword(account, encodedPassword)
    }

    @Test
    fun `fulfillPasswordChange - failure - account not found`() {
        //ARRANGE
        val requestHandleString = "019c477c-6d06-70a8-bcc5-eab25612b96c"
        val requestHandle = UUID.fromString(requestHandleString)
        val password = "Password-test"

        val dto = FulfillPasswordChangeDto(requestHandleString, password)

        given(requestHandleService.getAccountAndDiscardCheckedHandle(
            uuid = requestHandle,
            subject = RequestHandleSubject.CHANGE_PASSWORD,
            expirationTime = changePasswordExpiresIn
        )).willThrow(AccountNotFoundException::class.java)

        //ACT
        assertThrows<AccountNotFoundException> {
            service.fulfillPasswordChange(dto)
        }

    }

    @Test
    fun `fulfillPasswordChange - failure - password invalid`() {
        //ARRANGE
        val requestHandleString = "019c477c-6d06-70a8-bcc5-eab25612b96c"
        val password = "Password-test"

        val dto = FulfillPasswordChangeDto(requestHandleString, password)

        given(passwordValidator.validate(password))
            .willThrow(BadPasswordException::class.java)

        //ACT + ASSERT
        assertThrows<BadPasswordException> {
            service.fulfillPasswordChange(dto)
        }

    }

}