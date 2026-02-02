package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.account.ChangePasswordDto
import com.yourRPG.chatPG.dto.auth.account.CreateAccountDto
import com.yourRPG.chatPG.security.token.TokenManagerService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @InjectMocks
    lateinit var service: AuthService

    @Mock private lateinit var authChangePasswordService: AuthChangePasswordService
    @Mock private lateinit var authCreateAccountService: AuthCreateAccountService
    @Mock private lateinit var authLogInOutService: AuthLogInOutService
    @Mock private lateinit var tokenManagerService: TokenManagerService

    @Test
    fun login() {
        //ARRANGE
        val credentials = mock(LoginCredentials::class.java)
        val token = "tokenTest"
        val refreshToken = "refreshTokenTest"

        val pair = TokenDto(token) to refreshToken

        given(authLogInOutService.login(credentials))
            .willReturn(pair)

        //ACT
        val (responseTokenDto, responseRefreshToken) = service.login(credentials)

        //ASSERT
        assertEquals(token, responseTokenDto.token)
        assertEquals(refreshToken, responseRefreshToken)

    }

    @Test
    fun logout() {
        //ARRANGE
        val accountId = 1L

        //ACT
        service.logout(accountId)

        //ASSERT
        verify(authLogInOutService)
            .logout(accountId)

    }

    @Test
    fun requestChangePassword() {
        //ARRANGE
        val email = "email@email.com"

        //ACT
        service.requestChangePassword(email)

        //ASSERT
        verify(authChangePasswordService)
            .requestChangePassword(email)

    }

    @Test
    fun confirmChangePassword() {
        //ARRANGE
        val changePasswordDto = mock(ChangePasswordDto::class.java)

        //ACT
        service.confirmChangePassword(changePasswordDto)

        //ASSERT
        verify(authChangePasswordService)
            .confirmChangePassword(changePasswordDto)
    }

    @Test
    fun createAccount() {
        //ARRANGE
        val createAccountDto = mock(CreateAccountDto::class.java)

        //ACT
        service.createAccount(createAccountDto)

        //ASSERT
        verify(authCreateAccountService)
            .createAccount(createAccountDto)

    }

    @Test
    fun activateAccount() {
        //ARRANGE
        val uuidDto = mock(UuidDto::class.java)

        //ACT
        service.activateAccount(uuidDto)

        //ASSERT
        verify(authCreateAccountService)
            .activateAccount(uuidDto)

    }

    @Test
    fun refreshToken() {
        //ARRANGE
        val oldRefreshToken = "oldRefreshTokenTest"

        //ACT
        service.refreshToken(oldRefreshToken)

        //ASSERT
        verify(tokenManagerService)
            .refreshTokens(oldRefreshToken)

    }

}