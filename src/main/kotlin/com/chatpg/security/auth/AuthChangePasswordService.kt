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
    private val mimeHelper: MimeHelper,

    @param:Value($$"${security.request-handle.change-password-expires-in}")
    private val changePasswordExpiresIn: Duration,

    private val passwordValidator: PasswordValidator,
) {

    private companion object {
        val subject = RequestHandleSubject.CHANGE_PASSWORD
    }

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

    //Should be done asynchronously
    fun sendOpenPasswordChangeEmail(email: String, url: String) {
        emailService.sendMimeEmail(
            subject = "Reset password",
            to = email,
            html =
                mimeHelper.getTemplate(
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