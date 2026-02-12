package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.FulfillPasswordChangeDto
import com.yourRPG.chatPG.dto.auth.OpenPasswordChangeDto
import com.yourRPG.chatPG.infra.email.EmailService
import com.yourRPG.chatPG.infra.email.MimeHelper
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.validator.account.PasswordValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*

@Service
class AuthChangePasswordService(
    private val passwordEncoder: PasswordEncoder,
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val requestHandleService: RequestHandleService,

    private val frontendUriHelper: FrontendUriHelper,
    private val mimeHelper: MimeHelper,

    @param:Value("\${security.request-handle.change-password-expires-in}")
    private val changePasswordExpiresIn: Duration,

    private val passwordValidator: PasswordValidator
) {

    companion object {
        private val subject = RequestHandleSubject.CHANGE_PASSWORD
    }

    //FIXME: Vulnerable to timing attacks
    @Transactional
    fun openPasswordChange(dto: OpenPasswordChangeDto) {
        val account: Account =
            accountService.getByEmail(dto.email)

        val uuid = requestHandleService.newRequestHandle(account, subject)

        val url = frontendUriHelper.appendString("/login/forgot-password/$uuid")

        sendOpenPasswordChangeEmail(dto.email, url)
    }

    fun sendOpenPasswordChangeEmail(email: String, url: String) {
        emailService.sendMimeEmail(
            subject = "Reset password",
            to = email,
            html = mimeHelper.getTemplate(
                template  = "mime-change-password",
                variables = arrayOf("url" to url)
            )
        )
    }

    @Transactional
    fun fulfillPasswordChange(dto: FulfillPasswordChangeDto) {
        passwordValidator.validate(dto.password)

        val requestHandle = UUID.fromString(dto.requestHandle)

        val account: Account =
            requestHandleService.getAccountAndDiscardCheckedHandle(
                requestHandle,
                subject,
                changePasswordExpiresIn
            )

        val encodedPassword = passwordEncoder.encode(dto.password)
        accountService.updatePassword(account, encodedPassword)
    }

}