package com.yourRPG.chatPG.controller.auth

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.account.ChangePasswordDto
import com.yourRPG.chatPG.dto.auth.account.CreateAccountDto
import com.yourRPG.chatPG.infra.http.CookieService
import com.yourRPG.chatPG.security.auth.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val service: AuthService,
    private val cookieService: CookieService
) {

    /**
     * @see AuthService.login
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody credentials: LoginCredentials,
    ): ResponseEntity<TokenDto> {
        val (tokenDto, refreshToken) = service.login(credentials)

        val cookie = cookieService.refreshToken(refreshToken)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(tokenDto)
    }

    /**
     * Returns a [TokenDto] containing the generated access token, together with the refresh token inside an http-only
     *  cookie.
     *
     * @param oldRefreshToken the refresh token that will be used to identify the account requiring the refresh, and
     *  authorize that to be done.
     *      
     * @see AuthService.login
     */
    @PostMapping("/refresh-tokens")
    fun refreshTokens(
        @CookieValue("refresh_token") oldRefreshToken: String,
    ): ResponseEntity<TokenDto> {
        val (tokenDto, newRefreshToken) = service.refreshToken(oldRefreshToken)

        val cookie = cookieService.refreshToken(newRefreshToken)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(tokenDto)
    }

    /**
     * @see AuthService.requestChangePassword
     */
    @PostMapping("/request-change-password")
    fun requestChangePassword(
        @RequestBody email: String
    ): ResponseEntity<Unit> {
        service.requestChangePassword(email)
        return ResponseEntity.noContent().build()
    }

    /**
     * @see AuthService.requestChangePassword
     */
    @PostMapping("/confirm-change-password")
    fun confirmChangePassword(
        @RequestBody dto: ChangePasswordDto
    ): ResponseEntity<Unit> {
        service.confirmChangePassword(dto)
        return ResponseEntity.noContent().build()
    }

    /**
     * @see AuthService.createAccount
     */
    @PostMapping("/create-account")
    fun createAccount(
        @Valid @RequestBody dto: CreateAccountDto
    ): ResponseEntity<AccountDto> =
        ResponseEntity.ok(
            service.createAccount(dto)
        )

    /**
     * @see AuthService.activateAccount
     */
    @PostMapping("/activate-account")
    fun activateAccount(
        @Valid @RequestBody uuid: UuidDto
    ): ResponseEntity<AccountDto> =
        ResponseEntity.ok(
            service.activateAccount(uuid)
        )

}