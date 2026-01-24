package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import com.yourRPG.chatPG.helper.http.CookieService
import com.yourRPG.chatPG.service.account.AccountService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
class TokenServiceTest {

    private lateinit var tokenService: TokenService

    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var cookieService: CookieService

    private val secret = "secret"

    private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

    @Mock
    private lateinit var account: Account

    @BeforeEach
    fun setup() {
        tokenService = TokenService(accountService, cookieService, secret, clock)
    }

    @Test
    fun `signAccessToken - success`() {
        //ARRANGE
        val username = "username_test"

        given(account.username)
            .willReturn(username)

        //ACT
        val token = tokenService.signAccessToken(account)

        //ASSERT
        assertEquals(getIdClaim(token).toString(), "0")

        assertEquals(getSubject(token), username)
    }

    @Test
    fun `signAccessTokenWithLifetime - token IS NOT expired`() {
        //ARRANGE
        val username = "username_test"

        given(account.username)
            .willReturn(username)

        //ACT
        val token = tokenService.signAccessTokenWithLifetime(Duration.ofMinutes(10L), account)

        //ASSERT
        assertEquals(getIdClaim(token).toString(), "0")

        assertEquals(getSubject(token), username)
    }

    @Test
    fun `signAccessTokenWithLifetime - (Duration, Account) - token IS expired`() {
        //ARRANGE
        val username = "username_test"

        given(account.username)
            .willReturn(username)

        //ACT
        val token = tokenService.signAccessTokenWithLifetime(Duration.ofSeconds(0L), account)

        //ASSERT
        assertThrows<InvalidTokenException> {
            getIdClaim(token)
        }
    }

    private fun getIdClaim(token: String): Claim =
        runCatching {
            verifyToken(token)
                .claims["id"]
                ?: throw InvalidTokenException("Invalid claim")
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }

    private fun getSubject(token: String): String =
        runCatching {
            verifyToken(token).subject
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }

    private fun verifyToken(token: String): DecodedJWT =
        JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(tokenService.issuer)
            .build()
            .verify(token)

}