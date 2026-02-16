package com.yourRPG.chatPG.controller.auth

import com.yourRPG.chatPG.config.ApplicationEndpoints
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
@RequestMapping(ApplicationEndpoints.AuthSecure.BASE)
class AuthSecureController(
    private val service: AuthService,
) {

    /**
     * @see AuthService.logout
     */
    @PostMapping(ApplicationEndpoints.AuthSecure.LOGOUT)
    fun logout(
        @AuthenticationPrincipal accountId: Long
    ): ResponseEntity<TokenDto> {
        service.logout(accountId)
        return ResponseEntity.noContent().build()
    }

}