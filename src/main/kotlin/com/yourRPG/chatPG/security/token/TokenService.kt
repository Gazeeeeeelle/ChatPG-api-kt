package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import com.yourRPG.chatPG.helper.http.CookieService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration

@Service
class TokenService(
    private val accountService: AccountService,
    private val cookieService: CookieService,

    @param:Value("\${api.security.token.secret}")
    private val secret: String,

    private val clock: Clock = Clock.systemUTC()
) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    val issuer: String = "API ChatPG"

    /**
     * Generates and signs a new access token.
     *
     * @param account token owner
     * @return JWT's String value
     * @see signAccessTokenWithLifetime
     */
    fun signAccessToken(account: Account): String =
        signAccessTokenWithLifetime(10L.minutes(), account)

    /**
     * Generates JWT token owned by [account] that expires in [duration].
     *
     * @param duration token's lifetime.
     * @param account token owner.
     * @return JWT's String value.
     * @throws AccountNotFoundException if an account without an id was given.
     */
    fun signAccessTokenWithLifetime(duration: Duration, account: Account): String =
        createJwtExpiresIn(duration).apply {

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
    fun createJwtExpiresIn(duration: Duration): JWTCreator.Builder =
        JWT.create().apply {
            val now = clock.instant()
            val expiresAt = now.plus(duration)

            withIssuer(issuer)
            withIssuedAt(now)
            withExpiresAt(expiresAt)
        }

    /**
     * Refresh both the *Access Token*, returned inside TokenDto, and the *Refresh Token*, put as a cookie in the [HttpServletResponse]
     *  object given.
     *
     * @param response where to add the cookie.
     * @param oldRefresh refresh token used to check authenticity of the request.
     * @see AccountService.getByRefreshToken
     * @see appendNewRefreshToken
     * @throws com.yourRPG.chatPG.exception.UnauthorizedException if the [oldRefresh] refresh token given did not identify
     *  an account.
     */
    @Transactional
    fun refreshTokens(response: HttpServletResponse, oldRefresh: String): TokenDto {
        val account = accountService.getByRefreshToken(oldRefresh)

        val access = signAccessToken(account)

        appendNewRefreshToken(response, owner = account)

        return TokenDto(access)
    }

    /**
     * Adds a cookie with name *"refresh_token"* to the response. Its value is of the [newRefreshToken] generated with
     *  owner [owner].
     *
     * @param response where to add the cookie.
     * @param owner owner of the generated refresh token.
     * @see CookieService.appendCookie
     */
    fun appendNewRefreshToken(response: HttpServletResponse, owner: Account) {
        cookieService.appendCookie(response,
            name  = "refresh_token",
            value = newRefreshToken(owner)
        )
    }

    /**
     * Creates and signs a JWT token used as *Refresh Token*, which has longer lifetime, and is persisted in the [account]
     *  given.
     *
     * @param account where to persist the new token.
     * @return JWT's [String] value.
     * @see AccountService.saveWithRefreshToken
     */
    fun newRefreshToken(account: Account): String =
        signAccessTokenWithLifetime(7L.days(), account).apply {
            accountService.saveWithRefreshToken(account, refreshToken = this)
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
        runCatching {
            verifyToken(token)
                .claims[claim]
                ?: throw InvalidTokenException("Invalid claim")
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }

    /**
     * Verifies the token given and returns its decoded form.
     *
     * @param token JWT token to decode.
     * @return [DecodedJWT] form of the given token.
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the verification failed.
     * @see com.auth0.jwt.JWTVerifier.verify
     */
    fun verifyToken(token: String): DecodedJWT =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
            .verify(token)

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

    /**
     * Wrappers for [Duration]'s *"of"* methods to increase readability.
     */
    fun (Long).minutes(): Duration = Duration.ofMinutes(this)
    fun (Long).days()   : Duration = Duration.ofDays(this)

}
