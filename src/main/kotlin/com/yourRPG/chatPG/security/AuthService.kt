package com.yourRPG.chatPG.security

import com.yourRPG.chatPG.dto.account.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.security.token.AccountDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    /* Services */
    private val accountDetailsService: AccountDetailsService,
    private val tokenService: TokenService,

    /* Encoder */
    private val passwordEncoder: PasswordEncoder
) {

    fun login(credentials: LoginCredentials): TokenDto {

        val userDetails: UserDetails = accountDetailsService
            .loadUserByUsername(credentials.username)

        val passwordMatches: Boolean =
            passwordEncoder.matches(credentials.password, userDetails.password)

        if (passwordMatches) {
            return TokenDto(tokenService.generateToken(userDetails))
        }

        throw AccessToAccountUnauthorizedException("Wrong password")
    }

}
