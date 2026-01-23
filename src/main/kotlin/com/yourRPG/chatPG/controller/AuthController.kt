package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.account.CreateAccountDto
import com.yourRPG.chatPG.dto.auth.ChangePasswordDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.security.auth.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val service: AuthService,
) {

    /**
     * @see AuthService.login
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody credentials: LoginCredentials,
        response: HttpServletResponse
    ): ResponseEntity<TokenDto> =
        ResponseEntity.ok(
            service.login(response, credentials)
        )

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
     * @see AuthService.login
     */
    @PostMapping("/refreshToken")
    fun refreshToken(
        @CookieValue("refresh_token") oldRefresh: String,
        response: HttpServletResponse
    ): ResponseEntity<TokenDto> {
        val (access, refresh) = service.refreshToken(oldRefresh)
        response.addCookie(Cookie("refresh_token", refresh))
        return ResponseEntity.ok(
            TokenDto(access)
        )
    }

    /**
     * @see AuthService.requestChangePassword
     */
    @PostMapping("/requestChangePassword")
    fun requestChangePassword(
        @RequestBody email: String
    ): ResponseEntity<Unit> {
        service.requestChangePassword(email)
        return ResponseEntity.noContent().build()
    }

    /**
     * @see AuthService.requestChangePassword
     */
    @PostMapping("/confirmChangePassword")
    fun confirmChangePassword(
        @RequestBody dto: ChangePasswordDto
    ): ResponseEntity<Unit> {
        service.confirmChangePassword(dto)
        return ResponseEntity.noContent().build()
    }

    /**
     * @see AuthService.createAccount
     */
    @PostMapping("/createAccount")
    fun createAccount(
        @Valid @RequestBody dto: CreateAccountDto
    ): ResponseEntity<AccountDto> =
        ResponseEntity.ok(
            service.createAccount(dto)
        )

    /**
     * @see AuthService.activateAccount
     */
    @PostMapping("/activateAccount")
    fun activateAccount(
        @Valid @RequestBody uuid: UuidDto
    ): ResponseEntity<AccountDto?> =
        ResponseEntity.ok(
            service.activateAccount(uuid)
        )

}