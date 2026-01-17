package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.auth.ChangePasswordDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.security.AuthService
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

}