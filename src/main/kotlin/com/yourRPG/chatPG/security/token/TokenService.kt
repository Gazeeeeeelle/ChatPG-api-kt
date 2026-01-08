package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.Claim
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import com.yourRPG.chatPG.repository.AccountRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId

@Service
class TokenService(
    /* Repositories */
    private val accountRepository: AccountRepository,
) {

    @Value("\${api.security.token.secret}")
    private lateinit var secret: String

    private lateinit var algorithm: Algorithm

    private val issuer: String = "API ChatPG"

    @PostConstruct
    fun init() {
        algorithm = Algorithm.HMAC256(secret)
    }

    fun generateToken(userDetails: UserDetails): String {

        val account = accountRepository.findByNameEquals(userDetails.username)
            ?: throw AccountNotFoundException("Account not found")

        val now = Instant.now()
            .atZone(ZoneId.systemDefault())
            .toInstant()

        return JWT.create()
            .withIssuer(issuer)
            .withIssuedAt(now)
            .withExpiresAt(now.plusSeconds(600))
            .withSubject(account.username)
            .withClaim("id", account.id)
            .sign(algorithm)
    }

    fun getSubject(token: String): String? {
        try {
            return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .subject
        } catch (ex: JWTVerificationException) {
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }
    }

    fun getClaim(token: String, claim: String): Claim {
        try {
            return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .claims[claim]
                ?: throw InvalidTokenException("Invalid claim")
        } catch (ex: JWTVerificationException) {
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }
    }

}
