package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.security.AuthService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class AuthController(
    private val service: AuthService,
    private val accountService: AccountService
) {

    /**
     * @see AuthService.login
     */
    @PostMapping
    fun login(
        @Valid @RequestBody credentials: LoginCredentials
    ): ResponseEntity<TokenDto> =
        ResponseEntity.ok(
            service.login(credentials)
        )

    /**
     * @see AccountService.existsByName
     */
    @PostMapping("/exists")
    fun accountExists(
        @RequestBody accountName: String
    ): ResponseEntity<Boolean> =
        ResponseEntity.ok(
            accountService.existsByName(accountName)
        )

}