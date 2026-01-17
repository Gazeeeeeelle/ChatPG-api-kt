package com.yourRPG.chatPG.security

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.ChangePasswordDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.auth.PasswordResetException
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.security.token.AccountDetailsService
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.email.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    /* Services */
    private val accountDetailsService: AccountDetailsService,
    private val accountService: AccountService,
    private val tokenService: TokenService,

    private val emailService: EmailService,

    /* Encoder */
    private val passwordEncoder: PasswordEncoder
) {

    fun login(credentials: LoginCredentials): TokenDto = credentials.run {
        val userDetails: UserDetails = accountDetailsService
            .loadUserByUsername(username)

        passwordEncoder.matches(password, userDetails.password)
            .takeIf { it }
            ?: throw AccessToAccountUnauthorizedException("Wrong password")

        TokenDto(tokenService.generateToken(userDetails))
    }

    @Value("\${server.frontend.address}")
    private lateinit var frontEndAddress: String

    private val forgotPasswordPath = "/login/forgot-password"

    private val protocol = "http://"

    fun requestChangePassword(email: String) {
        val account: Account =
            accountService.getByEmail(email)

        val uuid = UUID.randomUUID()

        accountService.updateUuid(account, uuid)

        val html =
        """
        <h1>Do not click the following link if you did not request password reset!</h1>
        Click <a href='$protocol$frontEndAddress$forgotPasswordPath/$uuid'>here</a> to reset password.
        """.trimIndent()

        emailService.sendMimeEmail(
            subject = "Reset password",
            to = email,
            html
        )

    }

    fun confirmChangePassword(changePassword: ChangePasswordDto) = changePassword.run {
        val account: Account =
            accountService.getByUuid(UUID.fromString(changePassword.uuid))

        val birth = account.uuidBirth
            ?: throw PasswordResetException("Uuid does not have instant of creation")

        val fifteenMinutesAgo = Instant.now()
            .minus(Duration.ofMinutes(15))

        val stillValid = birth.isAfter(fifteenMinutesAgo)

        if (stillValid) {
            val encryptedPassword =
                passwordEncoder.encode(changePassword.password)

            accountService.updatePassword(account, encryptedPassword)
        }

        accountService.updateUuid(account, null)
    }

}
