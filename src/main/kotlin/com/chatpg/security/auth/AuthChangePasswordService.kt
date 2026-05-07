package com.chatpg.security.auth

import com.chatpg.domain.account.Account
import com.chatpg.dto.auth.FulfillPasswordChangeDto
import com.chatpg.dto.auth.OpenPasswordChangeDto
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.infra.email.EmailService
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
     * Opens a password change request by identifying the account by the email given, then generating a new request
     *  handle that is sent over by email to continue the process, verifying that the requester indeed has access to
     *  such email.
     *
     * @param dto Open password change request, which includes the email used to identify the account.
     * @see RequestHandleService.newRequestHandle
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
     * Sends MIME Email with template for Change Password to the email ([email]) with url ([url]) generated that
     *  includes the request handle.
     *
     * @param email
     * @param url
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
     * Changes the password of the account identified by the account's public UUID in [dto] to the password chosen,
     *  which is, as well, given in the [dto].
     *
     * @param dto Fulfill password change request, which includes the email used to identify the account and the
     *  password to update to.
     */
    @Transactional
    fun fulfillPasswordChange(dto: FulfillPasswordChangeDto) {
        passwordValidator.validate(dto.password)

        val requestHandle = UUID.fromString(dto.requestHandle)

        val account: Account =
            requestHandleService.getAccountAndDiscardCheckedHandle(
                requestHandle,
                subject,
                expirationTime = changePasswordExpiresIn
            )

        val encodedPassword = passwordEncoder.encode(dto.password)
        accountService.updatePassword(account, encodedPassword)
    }

}