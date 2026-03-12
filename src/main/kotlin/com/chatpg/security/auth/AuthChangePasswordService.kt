package com.chatpg.security.auth

import com.chatpg.domain.account.Account
import com.chatpg.dto.auth.FulfillPasswordChangeDto
import com.chatpg.dto.auth.OpenPasswordChangeDto
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.infra.email.EmailService
import com.chatpg.infra.email.MimeHelper
import com.chatpg.infra.uri.FrontendUriHelper
import com.chatpg.security.helper.NullSafePasswordEncoder
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.security.requesthandle.RequestHandleSubject
import com.chatpg.service.account.AccountService
import com.chatpg.validator.account.PasswordValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*

@Service
class AuthChangePasswordService(
    private val passwordEncoder: NullSafePasswordEncoder,
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val requestHandleService: RequestHandleService,

    private val frontendUriHelper: FrontendUriHelper,

    @param:Value($$"${security.request-handle.change-password-expires-in}")
    private val changePasswordExpiresIn: Duration,

    private val passwordValidator: PasswordValidator,
) {

    private companion object {
        val subject = RequestHandleSubject.CHANGE_PASSWORD
    }

    /**
     * TODO
     */
    @Transactional
    fun openPasswordChange(dto: OpenPasswordChangeDto) {
        val account: Account = try {
            accountService.getByEmail(dto.email)
        } catch (_: AccountNotFoundException) {
            return //Silent return, mitigating Account Enumeration
        }

        val uuid = requestHandleService.newRequestHandle(account, subject)

        val url = frontendUriHelper.appendString("/login/forgot-password/$uuid")

        sendOpenPasswordChangeEmail(dto.email, url)
    }

    /**
     * TODO
     */
    fun sendOpenPasswordChangeEmail(email: String, url: String) {
        emailService.sendMimeEmailWithTemplate(
            subject = "Reset password",
            to = email,
            templateName = "mime-change-password",
            "url" to url
        )
    }

    /**
     * TODO
     */
    @Transactional
    fun fulfillPasswordChange(dto: FulfillPasswordChangeDto) {
        passwordValidator.validate(dto.password)

        val uuid = UUID.fromString(dto.uuid)

        val account: Account =
            requestHandleService.getAccountAndDiscardCheckedHandle(
                uuid,
                subject,
                expirationTime = changePasswordExpiresIn
            )

        val encodedPassword = passwordEncoder.encode(dto.password)
        accountService.updatePassword(account, encodedPassword)
    }

}