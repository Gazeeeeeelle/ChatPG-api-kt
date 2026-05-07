package com.chatpg.security.token

import com.chatpg.domain.account.Account
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.exception.security.InvalidTokenException
import com.chatpg.service.account.AccountService
import helper.NullSafeMatchers.any
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class TokenManagerServiceTest {

    private lateinit var service: TokenManagerService

    private val tokenService = mock<TokenService>()
    private val accountService = mock<AccountService>()

    private val accessTokenExpiresIn = Duration.ofMinutes(10L)
    private val refreshTokenExpiresIn = Duration.ofDays(7L)

    @BeforeEach
    fun setUp() {
        service = TokenManagerService(tokenService, accountService, accessTokenExpiresIn, refreshTokenExpiresIn)
    }

    private val account = mock<Account>()

    @Nested
    inner class Success {

        @Test
        fun signAccessToken() {
            //ACT
            service.signAccessToken(account)

            //ASSERT
            verify(tokenService)
                .signTokenWithLifetime(Duration.ofMinutes(10L), account)
        }

        @Test
        fun refreshTokens() {
            //ARRANGE
            val oldRefreshToken = "oldRefreshTokenTest"
            val newRefreshToken = "newRefreshTokenTest"
            val newAccessToken  = "newAccessTokenTest"

            given(accountService.getByRefreshToken(oldRefreshToken))
                .willReturn(account)

            given(tokenService.signTokenWithLifetime(accessTokenExpiresIn, account))
                .willReturn(newAccessToken)

            given(tokenService.signTokenWithLifetime(refreshTokenExpiresIn, account))
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
                .signTokenWithLifetime(refreshTokenExpiresIn, account)

        }


        @Test
        fun signRefreshToken() {
            //ARRANGE
            val token = "tokenTest"

            given(tokenService.signTokenWithLifetime(Duration.ofDays(7L), account))
                .willReturn(token)

            //ACT
            service.signRefreshToken(account)

            //ASSERT
            verify(tokenService)
                .signTokenWithLifetime(refreshTokenExpiresIn, account)

            verify(accountService)
                .updateRefreshToken(account, token)

        }

    }

    @Nested
    inner class Failure {

        @Test
        fun refreshTokens() {
            //ARRANGE
            val oldRefreshToken = "oldRefreshTokenTest"
            val newRefreshToken = "newRefreshTokenTest"
            val newAccessToken  = "newAccessTokenTest"

            given(accountService.getByRefreshToken(oldRefreshToken))
                .willReturn(account)

            given(tokenService.signTokenWithLifetime(accessTokenExpiresIn, account))
                .willReturn(newAccessToken)

            given(tokenService.signTokenWithLifetime(refreshTokenExpiresIn, account))
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
                .signTokenWithLifetime(refreshTokenExpiresIn, account)

        }

        @Test
        fun `refreshTokens - verification failed`() {
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
        fun `refreshTokens - account with oldRefreshToken not found`() {
            //ARRANGE
            val oldRefreshToken = "oldRefreshTokenTest"

            given(accountService.getByRefreshToken(oldRefreshToken))
                .willThrow(AccountNotFoundException("test"))

            //ACT + ASSERT
            assertThrows<UnauthorizedException> {
                service.refreshTokens(oldRefreshToken)
            }

            verify(tokenService)
                .verify(oldRefreshToken)

            verify(tokenService, never())
                .signTokenWithLifetime(Duration.ZERO.any(), account.any())

        }

        @Test
        fun signRefreshToken() {
            //ARRANGE
            val token = "tokenTest"

            given(tokenService.signTokenWithLifetime(Duration.ofDays(7L), account))
                .willReturn(token)

            //ACT
            val response = service.signRefreshToken(account)

            //ASSERT
            verify(tokenService)
                .signTokenWithLifetime(refreshTokenExpiresIn, account)

            verify(accountService)
                .updateRefreshToken(account, token)

            assertEquals(token, response)

        }

    }

}