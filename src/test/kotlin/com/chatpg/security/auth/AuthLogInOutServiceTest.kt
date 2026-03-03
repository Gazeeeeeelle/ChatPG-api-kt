package com.chatpg.security.auth

import com.chatpg.domain.account.Account
import com.chatpg.dto.auth.LoginCredentials
import com.chatpg.exception.auth.A2fRequiredException
import com.chatpg.exception.http.UnauthorizedException
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.security.token.TokenManagerService
import com.chatpg.service.account.AccountService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class AuthLogInOutServiceTest {

    private lateinit var service: AuthLogInOutService

    @Mock private lateinit var passwordEncoder: PasswordEncoder
    @Mock private lateinit var tokenManagerService: TokenManagerService
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var authA2fService: AuthA2fService
    @Mock private lateinit var requestHandleService: RequestHandleService

    private val loginWithHandleExpiresIn: Duration = Duration.ofMinutes(1L)

    @BeforeEach
    fun setUp() {
        service = AuthLogInOutService(
            passwordEncoder,
            tokenManagerService,
            accountService,
            authA2fService,
            requestHandleService,
            loginWithHandleExpiresIn
        )
    }

    @Test
    fun `login - success`() {
        //ARRANGE
        val username        = "username_test"
        val email           = "email@email.com"
        val rawPassword     = "Password-test"
        val encodedPassword = "encoded-Password-test"

        val credentials = LoginCredentials(username, rawPassword)
        val account     = Account(username, email, encodedPassword)

        given(accountService.getByName(username))
            .willReturn(account)

        given(passwordEncoder.matches(credentials.password, account.password))
            .willReturn(true)

        //ACT
        service.login(credentials)

        //ASSERT
        verify(tokenManagerService)
            .signAccessAndRefreshTokens(account)
    }

    @Test
    fun `login - success - a2f`() {
        //ARRANGE
        val username        = "username_test"
        val email           = "email@email.com"
        val rawPassword     = "Password-test"
        val encodedPassword = "encoded-Password-test"

        val credentials = LoginCredentials(username, rawPassword)
        val account     = Account(username, email, encodedPassword)
        account.auth.a2f = true

        given(accountService.getByName(username))
            .willReturn(account)

        given(passwordEncoder.matches(credentials.password, account.password))
            .willReturn(true)

        given(authA2fService.requireA2f(account))
            .willThrow(A2fRequiredException::class.java)

        //ACT
        assertThrows<A2fRequiredException> {
            service.login(credentials)
        }

        //ASSERT
        verify(authA2fService)
            .requireA2f(account)

        verify(tokenManagerService, never())
            .signAccessAndRefreshTokens(account)

    }

    @Test
    fun  `login - account not found`() {
        //ARRANGE
        val username        = "username_test"
        val email           = "email@email.com"
        val rawPassword     = "Password-test"
        val encodedPassword = "encoded-Password-test"

        val credentials = LoginCredentials(username, rawPassword)
        val account     = Account(username, email, encodedPassword)

        assertThrows<UnauthorizedException> {
            service.login(credentials)
        }

        //ASSERT
        verify(tokenManagerService, never())
            .signAccessAndRefreshTokens(account)
    }

    @Test
    fun `login - wrong password`() {
        //ARRANGE
        val username        = "username_test"
        val email           = "email@email.com"
        val rawPassword     = "Password-test"
        val encodedPassword = "encoded-Password-test"

        val credentials = LoginCredentials(username, rawPassword)
        val account     = Account(username, email, encodedPassword)

        given(accountService.getByName(username))
            .willReturn(account)

        //ACT
        assertThrows<UnauthorizedException> {
            service.login(credentials)
        }

        //ASSERT
        verify(tokenManagerService, never())
            .signAccessAndRefreshTokens(account)
    }

    @Test
    fun logout() {
        //ARRANGE
        val account = mock(Account::class.java)

        given(accountService.getById(0L))
            .willReturn(account)

        //ACT
        service.logout(0L)

        //ASSERT
        verify(accountService)
            .updateRefreshToken(account, refreshToken = null)

    }

}