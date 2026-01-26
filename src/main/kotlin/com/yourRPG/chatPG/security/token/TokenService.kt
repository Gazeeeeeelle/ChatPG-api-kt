package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration

@Service
class TokenService(
    @param:Value("\${api.security.token.secret}")
    private val secret: String,

    private val clock: Clock = Clock.systemUTC()
) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    val issuer: String = "API ChatPG"

    /**
     * Generates JWT token owned by [account] that expires in [duration].
     *
     * @param duration token's lifetime.
     * @param account token owner.
     * @return JWT's String value.
     * @throws AccountNotFoundException if an account without an id was given.
     */
    fun signTokenWithLifetime(duration: Duration, account: Account): String =
        createJwtWithLifetime(duration).apply {

            withSubject(account.username)

            withClaim("id", account.id
                ?: throw AccountNotFoundException("ID not found"))

        }.sign(algorithm)

    /**
     * Creates the JWT token that expires in [duration].
     *
     * @param duration token's lifetime.
     * @return Still unsigned JWT token, i.e. unbuilt.
     */
    private fun createJwtWithLifetime(duration: Duration): JWTCreator.Builder =
        JWT.create().apply {
            val now = clock.instant()
            val expiresAt = now.plus(duration)

            withIssuer(issuer)
            withIssuedAt(now)
            withExpiresAt(expiresAt)
        }


    /**
     * Returns the [Claim] requested in [claim] from the token [token].
     *
     * @param token where to extract the claim from.
     * @param claim which claim to extract.
     * @return [Claim] found.
     * @throws InvalidTokenException if the extraction of the claim was not possible.
     */
    fun getClaim(token: String, claim: String): Claim =
        verify(token)
            .claims[claim]
            ?: throw InvalidTokenException("Invalid claim")

    /**
     * Verifies the token given and returns its decoded form.
     *
     * @param token JWT token to decode.
     * @return [DecodedJWT] form of the given token.
     * @throws InvalidTokenException if the verification failed.
     */
    fun verify(token: String): DecodedJWT =
        runCatching {
            buildJWTVerifier().verify(token)
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message)
        }

    fun buildJWTVerifier(): JWTVerifier =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .build()

    /**
     * Gets and returns the *Bearer Token* from the [HttpServletRequest] given.
     *
     * @param request where to extract the bearer token from.
     * @return Bearer token's [String] value.
     * @throws AccessToAccountUnauthorizedException if no *Authorization* header was found.
     */
    fun getAccessToken(request: HttpServletRequest): String =
        request.getHeader("Authorization")
            ?.replace("Bearer ", "")
            ?: throw AccessToAccountUnauthorizedException("Authorization header is missing")

}
