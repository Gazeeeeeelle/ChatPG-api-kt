package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.account.ChangePasswordDto
import com.yourRPG.chatPG.exception.auth.password.PasswordResetException
import com.yourRPG.chatPG.helper.email.MimeHelper
import com.yourRPG.chatPG.helper.uri.FrontendUriHelper
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.email.EmailService
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class AuthChangePasswordService(
    private val accountService: AccountService,
    private val mimeHelper: MimeHelper,
    private val frontendUriHelper: FrontendUriHelper,
    private val emailService: EmailService,
    private val passwordService: AuthPasswordService
) {

    fun requestChangePassword(email: String) {
        val account: Account =
            accountService.getByEmail(email)

        val uuid = UUID.randomUUID()

        accountService.updateUuid(account, uuid)

        val url = frontendUriHelper.append("/login/forgot-password/$uuid")
        val html = mimeHelper.getTemplate(
            template  = "mime-change-password",
            variables = arrayOf("url" to url)
        )

        emailService.sendMimeEmail(
            subject = "Reset password",
            to = email,
            html
        )

    }

    fun confirmChangePassword(changePassword: ChangePasswordDto): Unit = changePassword.run {
        val uuid = UUID.fromString(changePassword.uuid)

        val account: Account =
            accountService.getByUuid(uuid)

        accountService.updateUuid(account, null)

        val birth = account.uuidBirth
            ?: throw PasswordResetException("Uuid does not have instant of creation")

        val fifteenMinutes = Duration.ofMinutes(15)
        val fifteenMinutesAgo = Instant.now().minus(fifteenMinutes)

        val stillValid = birth.isAfter(fifteenMinutesAgo)

        if (stillValid) {
            val encryptedPassword = passwordService.encrypt(rawPassword = changePassword.password)

            accountService.updatePassword(account, encryptedPassword)
        }

    }

}