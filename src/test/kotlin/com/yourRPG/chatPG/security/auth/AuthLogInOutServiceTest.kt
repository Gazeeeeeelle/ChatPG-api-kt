package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class AuthLogInOutServiceTest {

    @InjectMocks
    private lateinit var service: AuthLogInOutService

    @Mock private lateinit var authPasswordService: AuthPasswordService
    @Mock private lateinit var tokenManagerService: TokenManagerService
    @Mock private lateinit var accountService: AccountService

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var response: HttpServletResponse

    @Test
    fun `login - success`() {
        //ARRANGE
        val username        = "username_test"
        val email           = "email@email.com"
        val rawPassword     = "Password-test"
        val encodedPassword = "encoded-Password-test"

        val token = "token_test"

        val credentials = LoginCredentials(username, rawPassword)
        val account     = Account(username, email, encodedPassword)

        given(accountService.getByName(username))
            .willReturn(account)

        given(authPasswordService.passwordEncoder)
            .willReturn(passwordEncoder)

        given(passwordEncoder.matches(rawPassword, encodedPassword))
            .willReturn(true)

        given(tokenManagerService.signAccessToken(account))
            .willReturn(token)

        //ACT
        service.login(response, credentials)

        //ASSERT
        verify(tokenManagerService)
            .appendNewRefreshToken(response, account)


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

        given(authPasswordService.passwordEncoder)
            .willReturn(passwordEncoder)

        //ACT
        assertThrows<AccessToAccountUnauthorizedException> {
            service.login(response, credentials)
        }

        //ASSERT
        verify(tokenManagerService, never())
            .appendNewRefreshToken(response, account)

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
            .saveWithRefreshToken(account, refreshToken = null)

    }

}