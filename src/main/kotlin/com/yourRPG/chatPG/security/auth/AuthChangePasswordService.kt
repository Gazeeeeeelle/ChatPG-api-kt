package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.account.ConfirmChangePasswordDto
import com.yourRPG.chatPG.infra.email.EmailService
import com.yourRPG.chatPG.infra.email.MimeHelper
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.service.account.AccountService
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

) {

    companion object {
        private val subject = RequestHandleSubject.CHANGE_PASSWORD
    }

    @Transactional
    fun requestChangePassword(email: String) {
        val account: Account =
            accountService.getByEmail(email)

        val uuid = requestHandleService.newRequestHandle(account, subject)

        val url = frontendUriHelper.appendString("/login/forgot-password/$uuid")
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

    @Transactional
    fun confirmChangePassword(dto: ConfirmChangePasswordDto): Unit = dto.run {
        val uuidV7 = UUID.fromString(dto.requestHandle)

        val account: Account =
            requestHandleService.getAccountAndDiscardCheckedHandle(uuidV7,
                subject,
                changePasswordExpiresIn
            )

        val encodedPassword = passwordEncoder.encode(dto.password)
        accountService.updatePassword(account, encodedPassword)
    }

}