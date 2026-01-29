package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class AuthLogInOutServiceTest {

    @InjectMocks
    private lateinit var service: AuthLogInOutService

    @Mock private lateinit var authPasswordService: AuthPasswordService
    @Mock private lateinit var tokenManagerService: TokenManagerService
    @Mock private lateinit var accountService: AccountService

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `login - success`() {
        //ARRANGE
        val username        = "username_test"
        val email           = "email@email.com"
        val rawPassword     = "Password-test"
        val encodedPassword = "encoded-Password-test"

        val accessToken = "access_token_test"
        val refreshToken = "refresh_token_test"

        val credentials = LoginCredentials(username, rawPassword)
        val account     = Account(username, email, encodedPassword)

        given(accountService.getByName(username))
            .willReturn(account)

        given(authPasswordService.passwordEncoder)
            .willReturn(passwordEncoder)

        given(passwordEncoder.matches(rawPassword, encodedPassword))
            .willReturn(true)

        given(tokenManagerService.signAccessToken(account))
            .willReturn(accessToken)

        given(tokenManagerService.newRefreshToken(account))
            .willReturn(refreshToken)

        //ACT
        val (responseAccessToken, responseRefreshToken) = service.login(credentials)

        //ASSERT
        assertEquals(accessToken, responseAccessToken.token)
        assertEquals(refreshToken, responseRefreshToken)

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
            service.login(credentials)
        }

        //ASSERT
        verify(tokenManagerService, never())
            .signAccessToken(account)

        verify(tokenManagerService, never())
            .newRefreshToken(account)

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