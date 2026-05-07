package com.chatpg.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.chatpg.domain.account.Account
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.exception.security.InvalidTokenException
import com.chatpg.security.config.JwtConfiguration
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration

@Service
class TokenService(
    private val clock: Clock = Clock.systemUTC(),

    private val algorithm: Algorithm,

    private val jwtVerifier: JWTVerifier
) {


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

            withClaim("id", account.publicId.toString())

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

            withIssuer(JwtConfiguration.ISSUER)
            withIssuedAt(now)
            withExpiresAt(expiresAt)
        }

    /**
     * Returns the [Claim] requested in [claim] from the token [token]. Returns null if [Claim] could not be extracted.
     *
     * @param token where to extract the claim from.
     * @param claim which claim to extract.
     * @return Nullable [Claim] found.
     * @throws InvalidTokenException if the extraction of the claim was not possible.
     */
    fun getClaim(token: String, claim: String): Claim? =
        verify(token).claims[claim]

    /**
     * Verifies the token given and returns its decoded form.
     *
     * @param token JWT token to decode.
     * @return [DecodedJWT] form of the given token.
     * @throws InvalidTokenException if the verification failed.
     */
    fun verify(token: String): DecodedJWT =
        try {
            jwtVerifier.verify(token)
        } catch (_: TokenExpiredException) {
            throw UnauthorizedException("Token expired")
        } catch (_: JWTVerificationException) {
            throw InvalidTokenException("Malformed token")
        }

    /**
     * Gets and returns the *Bearer Token* from the [HttpServletRequest] given.
     *
     * @param request where to extract the bearer token from.
     * @return Bearer token's [String] value.
     * @throws UnauthorizedException if no *Authorization* header was found.
     */
    fun getAccessToken(request: HttpServletRequest): String =
        request.getHeader("Authorization")
            ?.replace("Bearer ", "")
            ?: throw UnauthorizedException("Authorization header is missing")

}
