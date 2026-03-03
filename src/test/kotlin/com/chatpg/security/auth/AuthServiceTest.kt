package com.chatpg.security.auth

import com.chatpg.dto.auth.FulfillA2fDto
import com.chatpg.dto.auth.LoginCredentials
import com.chatpg.dto.auth.UuidDto
import com.chatpg.dto.auth.FulfillPasswordChangeDto
import com.chatpg.dto.auth.OpenAccountCreationDto
import com.chatpg.dto.auth.OpenPasswordChangeDto
import com.chatpg.security.token.AccessAndRefreshTokens
import com.chatpg.security.token.TokenManagerService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @InjectMocks
    lateinit var service: AuthService

    @Mock private lateinit var authA2fService: AuthA2fService
    @Mock private lateinit var authChangePasswordService: AuthChangePasswordService
    @Mock private lateinit var authCreateAccountService: AuthCreateAccountService
    @Mock private lateinit var authLogInOutService: AuthLogInOutService
    @Mock private lateinit var tokenManagerService: TokenManagerService

    @Test
    fun login() {
        //ARRANGE
        val credentials = mock(LoginCredentials::class.java)
        val accessToken = "tokenTest"
        val refreshToken = "refreshTokenTest"

        val pair = AccessAndRefreshTokens(accessToken, refreshToken)

        given(authLogInOutService.login(credentials))
            .willReturn(pair)

        //ACT
        val (responseTokenDto, responseRefreshToken) = service.login(credentials)

        //ASSERT
        assertEquals(accessToken, responseTokenDto.token)
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
    fun openPasswordChange() {
        //ARRANGE
        val email = "email@email.com"
        val openPasswordChangeDto = OpenPasswordChangeDto(email)

        //ACT
        service.openPasswordChange(openPasswordChangeDto)

        //ASSERT
        verify(authChangePasswordService)
            .openPasswordChange(openPasswordChangeDto)
    }

    @Test
    fun fulfillPasswordChange() {
        //ARRANGE
        val fulfillPasswordChangeDto = mock(FulfillPasswordChangeDto::class.java)

        //ACT
        service.fulfillPasswordChange(fulfillPasswordChangeDto)

        //ASSERT
        verify(authChangePasswordService)
            .fulfillPasswordChange(fulfillPasswordChangeDto)
    }

    @Test
    fun openAccountCreation() {
        //ARRANGE
        val openAccountCreationDto = mock(OpenAccountCreationDto::class.java)

        //ACT
        service.openAccountCreation(openAccountCreationDto)

        //ASSERT
        verify(authCreateAccountService)
            .openAccountCreation(openAccountCreationDto)
    }

    @Test
    fun fulfillAccountCreation() {
        //ARRANGE
        val uuidDto = mock(UuidDto::class.java)

        //ACT
        service.fulfillAccountCreation(uuidDto)

        //ASSERT
        verify(authCreateAccountService)
            .fulfillAccountCreation(uuidDto)
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

    @Test
    fun fulfillA2f() {
        //ARRANGE
        val dto = FulfillA2fDto(UUID.randomUUID(), "code")

        //ACT
        service.fulfillA2f(dto)

        //ASSERT
        verify(authA2fService)
            .fulfillA2f(dto)
    }

    @Test
    fun loginWithHandle() {
        //ARRANGE
        val uuid = UUID.randomUUID()
        val uuidString = uuid.toString()
        val uuidDto = UuidDto(uuidString)

        //ACT
        service.loginWithHandle(uuidDto)

        //ASSERT
        verify(authLogInOutService)
            .loginWithHandle(uuidDto)
    }

}