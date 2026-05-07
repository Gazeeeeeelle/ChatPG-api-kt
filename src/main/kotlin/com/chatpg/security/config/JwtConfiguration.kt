package com.chatpg.security.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfiguration(
    @param:Value($$"${security.token.secret}")
    private val secret: String
) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    companion object {
        const val ISSUER = "API ChatPG"
    }

    @Bean
    fun jwtVerifier(): JWTVerifier =
        JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()

    @Bean
    fun algorithm(): Algorithm = algorithm

}