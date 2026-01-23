package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration

@Service
class TokenService(
    private val accountService: AccountService,

    @param:Value("\${api.security.token.secret}")
    private val secret: String,

    private val clock: Clock = Clock.systemUTC()
) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    val issuer: String = "API ChatPG"

    fun signAccessToken(account: Account): String =
        signAccessTokenWithLifetime(10L.minutes(), account)

    fun signAccessTokenWithLifetime(duration: Duration, account: Account): String =
        createJwtExpiresIn(duration).apply {

            withSubject(account.username)

            withClaim("id", account.id
                ?: throw AccountNotFoundException("ID not found"))

        }.sign(algorithm)

    fun createJwtExpiresIn(duration: Duration): JWTCreator.Builder =
        JWT.create().apply {
            val now = clock.instant()
            val expiresAt = now.plus(duration)

            withIssuer(issuer)
            withIssuedAt(now)
            withExpiresAt(expiresAt)
        }

    fun refreshTokens(oldRefresh: String): Pair<String, String> {
        val account = accountService.getByRefreshToken(oldRefresh)

        val access = signAccessToken(account)
        val newRefresh = newRefreshToken(account)

        return access to newRefresh
    }

    fun newRefreshToken(account: Account): String =
        signAccessTokenWithLifetime(7L.days(), account).apply {
            accountService.saveWithRefreshToken(account, refreshToken = this)
        }

    fun getClaim(token: String, claim: String): Claim =
        runCatching {
            JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .claims[claim]
                ?: throw InvalidTokenException("Invalid claim")
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }

    fun getAccessToken(request: HttpServletRequest) =
        request.getHeader("Authorization")
            ?.replace("Bearer ", "")
            ?: throw AccessToAccountUnauthorizedException("Authorization header is missing")

    fun (Long).minutes(): Duration = Duration.ofMinutes(this)
    fun (Long).days(): Duration    = Duration.ofDays(this)


}
