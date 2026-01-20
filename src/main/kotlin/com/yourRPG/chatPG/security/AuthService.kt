package com.yourRPG.chatPG.security

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.account.CreateAccountDto
import com.yourRPG.chatPG.dto.auth.ChangePasswordDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.exception.ConflictException
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.auth.AccountActivationException
import com.yourRPG.chatPG.exception.auth.PasswordResetException
import com.yourRPG.chatPG.helper.frontend.FrontendUrlHelper
import com.yourRPG.chatPG.helper.email.MimeHelper
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.security.token.AccountDetailsService
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.account.AccountStatus
import com.yourRPG.chatPG.service.email.EmailService
import com.yourRPG.chatPG.validator.account.PasswordValidator
import com.yourRPG.chatPG.validator.account.UsernameValidator
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
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

    /* Helpers */
    private val mimeHelper: MimeHelper,
    private val frontendUrlHelper: FrontendUrlHelper,

    /* Validators */
    private val passwordValidator: PasswordValidator,
    private val usernameValidator: UsernameValidator,

    /* Encoder */
    private val passwordEncoder: PasswordEncoder
) {

    fun encryptPassword(rawPassword: String): String = passwordEncoder.encode(rawPassword)

    fun login(credentials: LoginCredentials): TokenDto = credentials.run {
        val userDetails: UserDetails = accountDetailsService
            .loadUserByUsername(username)

        passwordEncoder.matches(password, userDetails.password)
            .takeIf { it }
            ?: throw AccessToAccountUnauthorizedException("Wrong password")

        TokenDto(tokenService.generateToken(userDetails))
    }

    fun requestChangePassword(email: String) {
        val account: Account =
            accountService.getByEmail(email)

        val uuid = UUID.randomUUID()

        accountService.updateUuid(account, uuid)

        val html = mimeHelper.getTemplateWithVariables(
            template  = "mime-change-password",
            variables = arrayOf( "url" to frontendUrlHelper.append("/login/forgot-password/$uuid") )
        )

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
            .minus(Duration.ofMinutes(15)) //15 minutes

        val stillValid = birth.isAfter(fifteenMinutesAgo)

        if (stillValid) {
            val encryptedPassword = encryptPassword(rawPassword = changePassword.password)

            accountService.updatePassword(account, encryptedPassword)
        }

        accountService.updateUuid(account, null)
    }

    @Modifying
    @Transactional
    fun createAccount(dto: CreateAccountDto): AccountDto {

        passwordValidator.validate(t = dto.password)
        usernameValidator.validate(t = dto.username)

        val encryptedPassword = encryptPassword(rawPassword = dto.password)

        val account = accountService.saveAccountWith(dto.username, dto.email, encryptedPassword)

        val uuid = UUID.randomUUID()

        accountService.updateUuid(account, uuid)

        val html = mimeHelper.getTemplateWithVariables(
            template  = "mime-activate-account",
            "url" to frontendUrlHelper.append("/login/activate-account/$uuid")
        )

        emailService.sendMimeEmail(
            subject = "Activate account",
            to = dto.email,
            html
        )

        return accountService.dtoOf(c = account)
    }

    @Modifying
    @Transactional
    fun activateAccount(dto: UuidDto): AccountDto? {
        val account: Account = accountService.getByUuid(UUID.fromString(dto.uuid))

        when (account.status) {
            AccountStatus.DISABLED -> {}
            AccountStatus.ENABLED  -> throw ConflictException("Account already activated")
            AccountStatus.DELETED  -> throw ConflictException("This account is deleted")
        }

        val birth = account.uuidBirth
            ?: throw AccountActivationException("Uuid does not have instant of creation")

        val fifteenMinutesAgo = Instant.now()
            .minus(Duration.ofMinutes(30)) //30 minutes

        if (birth.isAfter(fifteenMinutesAgo)) {
            accountService.updateUuid(account, null)
            accountService.updateStatus(account, status = AccountStatus.ENABLED)
            return accountService.dtoOf(c = account)
        }

        accountService.deleteById(account.id)
        throw AccountActivationException("Request expired. Account deleted")
    }

}
