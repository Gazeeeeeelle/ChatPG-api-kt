package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.OpenAccountCreationDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.exception.auth.password.BadPasswordException
import com.yourRPG.chatPG.exception.auth.username.BadUsernameException
import com.yourRPG.chatPG.exception.http.ConflictException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.infra.email.EmailService
import com.yourRPG.chatPG.infra.email.MimeHelper
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.mapper.AccountMapper
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.account.AccountStatus
import com.yourRPG.chatPG.validator.account.PasswordValidator
import com.yourRPG.chatPG.validator.account.UsernameValidator
import helper.NullSafeMatchers.STRING_TYPE
import helper.NullSafeMatchers.any
import helper.NullSafeMatchers.eq
import helper.NullSafeMatchers.that
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class AuthCreateAccountServiceTest {

    private lateinit var service: AuthCreateAccountService

    @Mock private lateinit var passwordEncoder: PasswordEncoder
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var emailService: EmailService
    @Mock private lateinit var requestHandleService: RequestHandleService
    @Mock private lateinit var usernameValidator: UsernameValidator
    @Mock private lateinit var passwordValidator: PasswordValidator
    @Mock private lateinit var mimeHelper: MimeHelper
    @Mock private lateinit var frontendUriHelper: FrontendUriHelper
    @Mock private lateinit var accountMapper: AccountMapper

    private val activateAccountExpiresIn: Duration = Duration.ofMinutes(30L)
    private val activateAccountUrl: String = "/login/activate-account"

    @BeforeEach
    fun setUp() {
        service = AuthCreateAccountService(
            passwordEncoder,
            accountService,
            emailService,
            requestHandleService,
            usernameValidator,
            passwordValidator,
            mimeHelper,
            frontendUriHelper,
            accountMapper,
            activateAccountExpiresIn,
            activateAccountUrl
        )
    }

    @Test
    fun `openAccountCreation - success`() {
        //ARRANGE
        val username = "username_test"
        val email = "email@email.com"
        val password = "Password_test"
        val openAccountCreationDto = OpenAccountCreationDto(username, email, password)

        val encodedPassword = "encoded-password-test"
        val account = Account(username, email, password)
        val handle = UUID.randomUUID()

        val html = "html"
        val url = "url"

        given(passwordEncoder.encode(password))
            .willReturn(encodedPassword)

        given(accountService.insertAccount(username, email, encodedPassword))
            .willReturn(account)

        given(requestHandleService.newRequestHandle(account, RequestHandleSubject.ACTIVATE_ACCOUNT))
            .willReturn(handle)

        given(
            frontendUriHelper.appendString(
            STRING_TYPE.that { it.contains(handle.toString()) }
        )).willReturn(url)

        given(
            mimeHelper.getTemplate(
                template = STRING_TYPE.any(),
                ("url" to url).eq()
            )
        ).willReturn(html)

        //ACT
        service.openAccountCreation(openAccountCreationDto)

        //ASSERT
        verify(usernameValidator).validate(username)
        verify(passwordValidator).validate(password)

        verify(emailService)
            .sendMimeEmail(
                subject = "Activate account",
                to = email,
                html
            )

        verify(accountMapper)
            .toDto(account)
    }

    @Test
    fun `openAccountCreation - failure - invalid username`() {
        //ARRANGE
        val username = "username_test"
        val email = "email@email.com"
        val password = "Password_test"
        val openAccountCreationDto = OpenAccountCreationDto(username, email, password)

        given(usernameValidator.validate(username))
            .willThrow(BadUsernameException::class.java)

        //ACT + ASSERT
        assertThrows<BadUsernameException> {
            service.openAccountCreation(openAccountCreationDto)
        }

    }

    @Test
    fun `openAccountCreation - failure - invalid password`() {
        //ARRANGE
        val username = "username_test"
        val email = "email@email.com"
        val password = "Password_test"
        val openAccountCreationDto = OpenAccountCreationDto(username, email, password)

        given(passwordValidator.validate(password))
            .willThrow(BadPasswordException::class.java)

        //ACT + ASSERT
        assertThrows<BadPasswordException> {
            service.openAccountCreation(openAccountCreationDto)
        }

    }

    @Test
    fun `fulfillAccountCreation - success`() {
        //ARRANGE
        val uuidString = "019c479c-f26e-7d4d-931b-360c5b831a39"
        val uuidDto = UuidDto(uuidString)

        val username = "username_test"
        val email = "email@email.com"
        val password = "Password_test"
        val account = Account(username, email, password)

        given(
            requestHandleService.getAccountAndDiscardCheckedHandle(
                uuidDto.value,
                subject = RequestHandleSubject.ACTIVATE_ACCOUNT,
                expirationTime = activateAccountExpiresIn
            )
        ).willReturn(account)

        //ACT
        service.fulfillAccountCreation(uuidDto)

        //ASSERT
        verify(accountService)
            .updateStatus(account, AccountStatus.ENABLED)

        verify(accountMapper)
            .toDto(account)

        verify(accountService, never())
            .deleteById(0L)
    }

    @Test
    fun `fulfillAccountCreation - failure - account not found`() {
        //ARRANGE
        val uuidString = "019c479c-f26e-7d4d-931b-360c5b831a39"
        val uuidDto = UuidDto(uuidString)

        given(
            requestHandleService.getAccountAndDiscardCheckedHandle(
                uuidDto.value,
                subject = RequestHandleSubject.ACTIVATE_ACCOUNT,
                expirationTime = activateAccountExpiresIn
            )
        ).willThrow(UnauthorizedException::class.java)

        //ACT + ASSERT
        assertThrows<UnauthorizedException> {
            service.fulfillAccountCreation(uuidDto)
        }

    }

    @TestFactory
    fun `fulfillAccountCreation - failure - abnormal - unallowed state`(): Stream<DynamicTest> {
        //ARRANGE
        val uuidString = "019c479c-f26e-7d4d-931b-360c5b831a39"
        val uuidDto = UuidDto(uuidString)

        val username = "username_test"
        val email = "email@email.com"
        val password = "Password_test"
        val account = Account(username, email, password)

        return Stream.of(
            AccountStatus.ENABLED, AccountStatus.DELETED
        ).map { status ->
            DynamicTest.dynamicTest("Status: $status") {
                //ARRANGE
                account.status = status

                given(
                    requestHandleService.getAccountAndDiscardCheckedHandle(
                        uuidDto.value,
                        subject = RequestHandleSubject.ACTIVATE_ACCOUNT,
                        expirationTime = activateAccountExpiresIn
                    )
                ).willReturn(account)

                //ACT + ASSERT
                assertThrows<ConflictException> {
                    service.fulfillAccountCreation(uuidDto)
                }
            }
        }
    }

}