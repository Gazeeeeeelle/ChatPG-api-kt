package com.yourRPG.chatPG.controller.auth

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.FulfillPasswordChangeDto
import com.yourRPG.chatPG.dto.auth.OpenAccountCreationDto
import com.yourRPG.chatPG.dto.auth.OpenPasswordChangeDto
import com.yourRPG.chatPG.infra.http.CookieService
import com.yourRPG.chatPG.security.auth.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApplicationEndpoints.Auth.BASE)
class AuthController(
    private val service: AuthService,
    private val cookieService: CookieService,
) {

    /**
     * @see AuthService.login
     */
    @PostMapping(ApplicationEndpoints.Auth.LOGIN)
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
    @PostMapping(ApplicationEndpoints.Auth.REFRESH_TOKENS)
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
     * @see AuthService.openPasswordChange
     */
    @PostMapping(ApplicationEndpoints.Auth.OPEN_PASSWORD_CHANGE)
    fun openPasswordChange(
        @Valid @RequestBody dto: OpenPasswordChangeDto
    ): ResponseEntity<Unit> {
        service.openPasswordChange(dto)
        return ResponseEntity.noContent().build()
    }

    /**
     * @see AuthService.fulfillPasswordChange
     */
    @PostMapping(ApplicationEndpoints.Auth.FULFILL_PASSWORD_CHANGE)
    fun fulfillPasswordChange(
        @RequestBody dto: FulfillPasswordChangeDto
    ): ResponseEntity<Unit> {
        service.fulfillPasswordChange(dto)
        return ResponseEntity.noContent().build()
    }

    /**
     * @see AuthService.openAccountCreation
     */
    @PostMapping(ApplicationEndpoints.Auth.OPEN_ACCOUNT_CREATION)
    fun openAccountCreation(
        @Valid @RequestBody dto: OpenAccountCreationDto
    ): ResponseEntity<AccountDto> =
        ResponseEntity.ok(
            service.openAccountCreation(dto)
        )

    /**
     * @see AuthService.fulfillAccountCreation
     */
    @PostMapping(ApplicationEndpoints.Auth.FULFILL_ACCOUNT_CREATION)
    fun fulfillAccountCreation(
        @Valid @RequestBody uuid: UuidDto
    ): ResponseEntity<AccountDto> =
        ResponseEntity.ok(
            service.fulfillAccountCreation(uuid)
        )

}