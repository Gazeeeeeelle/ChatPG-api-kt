package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import com.yourRPG.chatPG.service.account.AccountService
import helper.NullSafeMatchers.any
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class TokenManagerServiceTest {

    @InjectMocks
    private lateinit var service: TokenManagerService

    @Mock private lateinit var tokenService: TokenService
    @Mock private lateinit var accountService: AccountService

    @Mock
    private lateinit var account: Account

    @Test
    fun signAccessToken() {
        //ACT
        service.signAccessToken(account)

        //ASSERT
        verify(tokenService)
            .signTokenWithLifetime(Duration.ofMinutes(10L), account)
    }

    @Test
    fun `refreshTokens - success`() {
        //ARRANGE
        val oldRefreshToken = "oldRefreshTokenTest"
        val newRefreshToken = "newRefreshTokenTest"
        val newAccessToken  = "newAccessTokenTest"

        given(accountService.getByRefreshToken(oldRefreshToken))
            .willReturn(account)

        given(tokenService.signTokenWithLifetime(Duration.ofMinutes(10L), account))
            .willReturn(newAccessToken)

        given(tokenService.signTokenWithLifetime(Duration.ofDays(7L), account))
            .willReturn(newRefreshToken)

        //ACT
        val (responseAccessToken, responseRefreshToken) = service.refreshTokens(oldRefreshToken)

        //ASSERT
        assertEquals(newAccessToken, responseAccessToken.token)
        assertEquals(newRefreshToken, responseRefreshToken)

        verify(tokenService)
            .verify(oldRefreshToken)

        verify(accountService)
            .getByRefreshToken(oldRefreshToken)

        verify(tokenService)
            .signTokenWithLifetime(Duration.ofMinutes(10L), account)

    }

    @Test
    fun `refreshTokens - failure - verification failed`() {
        //ARRANGE
        val oldRefreshToken = "oldRefreshTokenTest"

        given(tokenService.verify(oldRefreshToken))
            .willThrow(InvalidTokenException("Invalid token"))

        //ACT + ASSERT
        assertThrows<InvalidTokenException> {
            service.refreshTokens(oldRefreshToken)
        }

        verify(tokenService)
            .verify(oldRefreshToken)

        verify(tokenService, never())
            .signTokenWithLifetime(Duration.ZERO.any(), account.any())

    }

    @Test
    fun `refreshTokens - failure - account with oldRefreshToken not found`() {
        //ARRANGE
        val oldRefreshToken = "oldRefreshTokenTest"

        given(accountService.getByRefreshToken(oldRefreshToken))
            .willThrow(AccountNotFoundException("test"))

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.refreshTokens(oldRefreshToken)
        }

        verify(tokenService)
            .verify(oldRefreshToken)

        verify(tokenService, never())
            .signTokenWithLifetime(Duration.ZERO.any(), account.any())

    }

    @Test
    fun newRefreshToken() {
        //ARRANGE
        val token = "tokenTest"

        given(tokenService.signTokenWithLifetime(Duration.ofDays(7L), account))
            .willReturn(token)

        //ACT
        service.newRefreshToken(account)

        //ASSERT
        verify(tokenService)
            .signTokenWithLifetime(Duration.ofDays(7L), account)

        verify(accountService)
            .saveWithRefreshToken(account, token)

    }

}