package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.FulfillPasswordChangeDto
import com.yourRPG.chatPG.dto.auth.OpenPasswordChangeDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.auth.password.BadPasswordException
import com.yourRPG.chatPG.infra.email.EmailService
import com.yourRPG.chatPG.infra.email.MimeHelper
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.validator.account.PasswordValidator
import helper.NullSafeMatchers.STRING_TYPE
import helper.NullSafeMatchers.any
import helper.NullSafeMatchers.eq
import helper.NullSafeMatchers.that
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import java.util.*

@ExtendWith(MockitoExtension::class)
class AuthChangePasswordServiceTest {

    private lateinit var service: AuthChangePasswordService

    @Mock private lateinit var passwordEncoder: PasswordEncoder
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var emailService: EmailService
    @Mock private lateinit var requestHandleService: RequestHandleService
    @Mock private lateinit var frontendUriHelper: FrontendUriHelper
    @Mock private lateinit var mimeHelper: MimeHelper
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
            mimeHelper,
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
        val html = "html"

        given(accountService.getByEmail(email))
            .willReturn(account)

        given(requestHandleService.newRequestHandle(account, subject))
            .willReturn(uuid)

        given(frontendUriHelper.appendString(STRING_TYPE.any()))
            .willReturn(url)

        given(mimeHelper.getTemplate(STRING_TYPE.any(), ("url" to url).eq()))
            .willReturn(html)

        //ACT
        service.openPasswordChange(dto)

        //ASSERT
        verify(mimeHelper)
            .getTemplate(STRING_TYPE.any(), ("" to "").any())

        verify(accountService)
            .getByEmail(email)

        verify(requestHandleService)
            .newRequestHandle(account, subject)

        verify(frontendUriHelper)
            .appendString(STRING_TYPE.that { it.contains(uuid.toString()) })

        verify(emailService)
            .sendMimeEmail("Reset password", email, html)
    }

    @Test
    fun `openPasswordChange - failure - account not found`() {
        //ARRANGE
        val email = "email@email.com"
        val dto = OpenPasswordChangeDto(email)

        given(accountService.getByEmail(email))
            .willThrow(AccountNotFoundException::class.java)

        //ACT
        assertThrows<AccountNotFoundException> {
            service.openPasswordChange(dto)
        }
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