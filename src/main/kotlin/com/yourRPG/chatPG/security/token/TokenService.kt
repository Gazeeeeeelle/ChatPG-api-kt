package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration

@Service
class TokenService(
    /* Services */
    private val accountService: AccountService,

    /* Values */
    @param:Value("\${api.security.token.secret}")
    private val secret: String,

    /* Clock */
    private val clock: Clock = Clock.systemUTC()
) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    private val issuer: String = "API ChatPG"

    fun generateToken(userDetails: UserDetails): String {
        val account = accountService.getByName(userDetails.username)

        val now = clock.instant()
        val expiresAt = now.plus(Duration.ofMinutes(10))

        return JWT.create().apply {
            withIssuer(issuer)
            withIssuedAt(now)
            withExpiresAt(expiresAt)
            withSubject(account.username)
            withClaim("id", account.id
                ?: throw AccountNotFoundException("ID not found"))
        }.sign(algorithm)
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

}
