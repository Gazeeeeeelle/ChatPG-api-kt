package com.yourRPG.chatPG.helper.http

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.security.token.TokenService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class CookieService(
    private val tokenService: TokenService,
) {

    fun appendRefreshTokenCookie(response: HttpServletResponse, account: Account) {
        response.addCookie(Cookie(
            "refresh_token",
            tokenService.newRefreshToken(account)
        ).apply {
            secure = false
            isHttpOnly = true
        })
    }

}