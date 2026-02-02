package com.yourRPG.chatPG.controller.auth

import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.infra.http.CookieService
import com.yourRPG.chatPG.security.auth.AuthService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * [AuthSecureController] is responsible for auth related endpoints that need authentication.
 */
@RestController
@RequestMapping("/auth/secure")
class AuthSecureController(
    private val service: AuthService,
    private val cookieService: CookieService
) {

    /**
     * @see AuthService.logout
     */
    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal accountId: Long
    ): ResponseEntity<TokenDto> {
        service.logout(accountId)
        return ResponseEntity.noContent().build()
    }

    /**
     * @see AuthService.requireRefreshToken
     */
    @PostMapping("/require-refresh-token")
    fun requireRefreshToken(
        @AuthenticationPrincipal accountId: Long,
    ): ResponseEntity<TokenDto> {
        val (tokenDto, refreshToken) = service.requireRefreshToken(accountId)

        val cookie = cookieService.refreshToken(refreshToken)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(tokenDto)
    }

}