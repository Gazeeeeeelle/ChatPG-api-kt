package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import com.yourRPG.chatPG.helper.http.CookieService
import com.yourRPG.chatPG.service.account.AccountService
import helper.NullSafeMatchers.STRING_TYPE
import helper.NullSafeMatchers.any
import helper.NullSafeMatchers.eq
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class TokenManagerServiceTest {

    @InjectMocks
    private lateinit var service: TokenManagerService

    @Mock private lateinit var tokenService: TokenService
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var cookieService: CookieService

    @Mock
    private lateinit var account: Account

    @Mock
    private lateinit var response: HttpServletResponse

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
        val newAccessToken  = "newAccessTokenTest"

        given(accountService.getByRefreshToken(oldRefreshToken))
            .willReturn(account)

        given(service.signAccessToken(account))
            .willReturn(newAccessToken)

        //ACT
        service.refreshTokens(response, oldRefreshToken)

        //ASSERT
        verify(tokenService)
            .verify(oldRefreshToken)

        verify(cookieService)
            .appendCookie(response.eq(), name = "refresh_token".eq(), value = STRING_TYPE.any())

    }

    @Test
    fun `refreshTokens - failure - verification failed`() {
        //ARRANGE
        val oldRefreshToken = "oldRefreshTokenTest"

        given(tokenService.verify(oldRefreshToken))
            .willThrow(InvalidTokenException("Invalid token"))

        //ACT + ASSERT
        assertThrows<InvalidTokenException> {
            service.refreshTokens(response, oldRefreshToken)
        }

        verify(tokenService)
            .verify(oldRefreshToken)

    }

    @Test
    fun `refreshTokens - failure - account with oldRefreshToken not found`() {
        //ARRANGE
        val oldRefreshToken = "oldRefreshTokenTest"

        given(accountService.getByRefreshToken(oldRefreshToken))
            .willThrow(AccountNotFoundException("test"))

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.refreshTokens(response, oldRefreshToken)
        }

        verify(tokenService)
            .verify(oldRefreshToken)

    }

    @Test
    fun appendNewRefreshToken() {
        //ACT
        service.appendNewRefreshToken(response, owner = account)

        //ASSERT
        verify(cookieService)
            .appendCookie(response.eq(), name = "refresh_token".eq(), value = STRING_TYPE.any())

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