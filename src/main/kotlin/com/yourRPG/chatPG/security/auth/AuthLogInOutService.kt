package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.helper.http.CookieService
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class AuthLogInOutService(
    private val authPasswordService: AuthPasswordService,
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val cookieService: CookieService
) {

    fun login(response: HttpServletResponse, credentials: LoginCredentials): TokenDto {
        val account = accountService.getByName(credentials.username)

        authPasswordService.passwordEncoder.matches(credentials.password, account.password)
            .takeIf { it }
            ?: throw AccessToAccountUnauthorizedException("Wrong password")

        cookieService.appendRefreshTokenCookie(response, account)

        val token = tokenService.signAccessToken(account)

        return TokenDto(token)
    }

    fun logout(accountId: Long) =
        accountService.getById(accountId).let { account ->
            accountService.saveWithRefreshToken(account, refreshToken = null)
        }

}