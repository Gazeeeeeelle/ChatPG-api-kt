package com.chatpg.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.chatpg.domain.account.Account
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.exception.security.InvalidTokenException
import com.chatpg.security.config.JwtConfiguration
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
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

    private val secret = "test-secret"

    private val jwtConfiguration = JwtConfiguration(secret)

    private val clock       = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    private val algorithm   = jwtConfiguration.algorithm()
    private val jwtVerifier = jwtConfiguration.jwtVerifier()

    private val service = TokenService(clock, algorithm, jwtVerifier)

    @Nested
    inner class Success {

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
        fun `getAccessToke - 'Bearer' included`() {
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
        fun verify() {
            //ARRANGE
            val username = "username_test"
            val account = Account(username, "email@email.com", "password")

            val token = service.signTokenWithLifetime(Duration.ofSeconds(10L), account)

            //ACT
            val decoded = service.verify(token)

            //ASSERT
            assertEquals(account.publicId.toString(), decoded.claims["id"]?.asString())
            assertEquals(username                   , decoded.subject)
            assertEquals(JwtConfiguration.ISSUER    , decoded.issuer)
        }

    }

    @Nested
    inner class Failure {

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
        fun getClaim() {
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
        fun `getAccessToken - no Authorization header`() {
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
        fun `verify - expired`() {
            //ARRANGE
            val account = Account("username_test", "email@email.com", "password")

            val token = service.signTokenWithLifetime(Duration.ofSeconds(0L), account)

            //ACT + ASSERT
            assertThrows<UnauthorizedException> {
                service.verify(token)
            }

        }

    }

    /**
     * Retrieves the claim "id" from the token given.
     *
     * @param token Token to extract claim from.
     * @return "id" claim found.
     * @throws InvalidTokenException If failed to retrieve claim "id" from token.
     */
    private fun getIdClaim(token: String): Claim =
        try {
            verify(token)
                .claims["id"]
                ?: throw InvalidTokenException("Claim 'id' not found")
        } catch (ex: JWTVerificationException) {
            throw InvalidTokenException(ex.message ?: "Failed to verify token")
        } catch (ex: InvalidClaimException) {
            throw InvalidTokenException(ex.message ?: "")
        }

    private fun getSubject(token: String): String =
        runCatching {
            verify(token).subject
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }

    private fun verify(token: String): DecodedJWT =
        JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(JwtConfiguration.ISSUER)
            .build()
            .verify(token)

}