package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
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

    @BeforeEach
    fun setup() {
        service = TokenService(secret, clock)
    }

    @Test
    fun `signTokenWithLifetime - success`() {
        //ARRANGE
        val account = Account("username_test", "email@email.com", "password")

        //ACT
        val token = service.signTokenWithLifetime(Duration.ofMinutes(10L), account)

        //ASSERT
        assertEquals(account.publicId.toString(), getIdClaim(token).asString())

        assertEquals(account.username, getSubject(token))

    }

    @Test
    fun `signTokenWithLifetime - expired`() {
        //ARRANGE
        val account = Account("username_test", "email@email.com", "password")

        //ACT
        val token = service.signTokenWithLifetime(Duration.ofSeconds(0L), account)

        //ASSERT
        assertThrows<InvalidTokenException> {
            getIdClaim(token)
        }
    }

    @Test
    fun `getClaim - success`() {
        //ARRANGE
        val account = Account("username_test", "email@email.com", "password")

        val token = service.signTokenWithLifetime(Duration.ofSeconds(10L), account)

        //ACT
        val claim = service.getClaim(token, "id")

        //ASSERT
        assertEquals(account.publicId.toString(), claim?.asString())
    }

    @Test
    fun `getClaim - failure`() {
        //ARRANGE
        val account = Account("username_test", "email@email.com", "password")

        val token = service.signTokenWithLifetime(Duration.ofSeconds(10L), account)
        val claim = "some_other_claim"

        //ACT + ASSERT
        val responseClaim = service.getClaim(token, claim)

        //ASSERT
        assertEquals(null, responseClaim)
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
        assertThrows<UnauthorizedException> {
            service.getAccessToken(request)
        }

    }

    @Test
    fun `verify - success`() {
        //ARRANGE
        val username = "username_test"
        val account = Account(username, "email@email.com", "password")

        val token = service.signTokenWithLifetime(Duration.ofSeconds(10L), account)

        //ACT
        val decoded = service.verify(token)

        //ASSERT
        assertEquals(account.publicId.toString(), decoded.claims["id"]?.asString())

        assertEquals(username, decoded.subject)

        assertEquals(service.issuer, decoded.issuer)

    }

    @Test
    fun `verify - failure - expired`() {
        //ARRANGE
        val account = Account("username_test", "email@email.com", "password")

        val token = service.signTokenWithLifetime(Duration.ofSeconds(0L), account)

        //ACT + ASSERT
        assertThrows<UnauthorizedException> {
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