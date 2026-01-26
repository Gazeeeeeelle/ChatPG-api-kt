package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
class TokenServiceTest {

    private lateinit var service: TokenService

    private val secret = "secret"

    private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

    @Mock
    private lateinit var account: Account

    @BeforeEach
    fun setup() {
        service = TokenService(secret, clock)
    }

    @Test
    fun signTokenWithLifetime() {
        //ARRANGE
        val username = "username_test"

        given(account.username)
            .willReturn(username)

        //ACT
        val token = service.signTokenWithLifetime(Duration.ofMinutes(10L), account)

        //ASSERT
        assertEquals(getIdClaim(token).toString(), "0")

        assertEquals(getSubject(token), account.username)

    }

    @Test
    fun `signTokenWithLifetime - expired`() {
        //ACT
        val token = service.signTokenWithLifetime(Duration.ofSeconds(0L), account)

        //ASSERT
        assertThrows<InvalidTokenException> {
            getIdClaim(token)
        }
    }

    @Test
    fun `signTokenWithLifetime - failure - abnormal - null account id`() {
        //ARRANGE
        given(account.id)
            .willReturn(null)

        //ACT + ASSERT
        assertThrows<AccountNotFoundException> {
            service.signTokenWithLifetime(Duration.ofSeconds(0L), account)
        }
    }

    @Test
    fun `getClaim - success`() {
        //ARRANGE
        val token = service.signTokenWithLifetime(Duration.ofSeconds(10L), account)
        val claimName = "id"

        //ACT
        val claim = service.getClaim(token, claimName)

        //ASSERT
        assertEquals("0", claim.toString())
    }

    @Test
    fun `getClaim - failure`() {
        //ARRANGE
        val token = service.signTokenWithLifetime(Duration.ofSeconds(10L), account)
        val claim = "some_other_claim"

        //ACT + ASSERT
        assertThrows<InvalidTokenException> {
            service.getClaim(token, claim)
        }
    }

    @Test
    fun `getAccessToken - success`() {
        //ARRANGE
        val request = mock(HttpServletRequest::class.java)
        val token = "tokenTest"

        given(request.getHeader("Authorization"))
            .willReturn(token)

        //ACT
        val response = service.getAccessToken(request)

        //ASSERT
        assertEquals(token, response)

    }

    @Test
    fun `getAccessToken - success - 'Bearer' included`() {
        //ARRANGE
        val request = mock(HttpServletRequest::class.java)
        val token = "tokenTest"

        given(request.getHeader("Authorization"))
            .willReturn("Bearer $token")

        //ACT
        val response = service.getAccessToken(request)

        //ASSERT
        assertEquals(token, response)

    }

    @Test
    fun `getAccessToken - failure - no Authorization header`() {
        //ARRANGE
        val request = mock(HttpServletRequest::class.java)

        given(request.getHeader("Authorization"))
            .willReturn(null)

        //ACT + ASSERT
        assertThrows<AccessToAccountUnauthorizedException> {
            service.getAccessToken(request)
        }

    }

    @Test
    fun `verify - success`() {
        //ARRANGE
        val username = "username_test"

        given(account.username)
            .willReturn(username)

        val token = service.signTokenWithLifetime(Duration.ofSeconds(10L), account)

        //ACT
        val decoded = service.verify(token)

        //ASSERT
        assertEquals("0", decoded.claims["id"].toString())

        assertEquals(username, decoded.subject)

        assertEquals(service.issuer, decoded.issuer)

    }

    @Test
    fun `verify - failure - expired`() {
        //ARRANGE
        val token = service.signTokenWithLifetime(Duration.ofSeconds(0L), account)

        //ACT + ASSERT
        assertThrows<InvalidTokenException> {
            service.verify(token)
        }

    }

    private fun getIdClaim(token: String): Claim =
        runCatching {
            verify(token)
                .claims["id"]
                ?: throw InvalidTokenException("Invalid claim")
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }

    private fun getSubject(token: String): String =
        runCatching {
            verify(token).subject
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }

    private fun verify(token: String): DecodedJWT =
        JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(service.issuer)
            .build()
            .verify(token)

}