package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.exception.auth.A2FRequiredException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
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

@ExtendWith(MockitoExtension::class)
class AuthLogInOutServiceTest {

    @InjectMocks
    private lateinit var service: AuthLogInOutService

    @Mock private lateinit var passwordEncoder: PasswordEncoder
    @Mock private lateinit var tokenManagerService: TokenManagerService
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var authA2FService: AuthA2FService

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
            .requireRefreshToken(account)
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

        given(authA2FService.requireA2F(account))
            .willThrow(A2FRequiredException::class.java)

        //ACT
        assertThrows<A2FRequiredException> {
            service.login(credentials)
        }

        //ASSERT
        verify(authA2FService)
            .requireA2F(account)

        verify(tokenManagerService, never())
            .requireRefreshToken(account)

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
            .requireRefreshToken(account)
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
            .requireRefreshToken(account)
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