package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.FulfillA2FRequestDto
import com.yourRPG.chatPG.exception.auth.A2FRequiredException
import com.yourRPG.chatPG.infra.email.EmailService
import com.yourRPG.chatPG.infra.email.MimeHelper
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.security.token.TokenManagerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.time.Duration
import java.util.*

@Service
class AuthA2FService(
    private val requestHandleService: RequestHandleService,
    private val emailService: EmailService,
    private val tokenManagerService: TokenManagerService,

    private val mimeHelper: MimeHelper,
    private val frontendUriHelper: FrontendUriHelper,

    @param:Value("\${security.request-handle.a2f-expires-in}")
    private val a2FRequestExpiresIn: Duration,
) {

    companion object {
        private val subject = RequestHandleSubject.A2F
    }

    fun getUrlString(handle: UUID): String =
        frontendUriHelper.run {
            appendString("$a2fUrl?uuid=$handle")
        }

    fun getUrl(handle: UUID): URI = URI.create(getUrlString(handle))

    @Transactional
    fun openA2FRequest(account: Account): UUID {
        val email = requireNotNull(account.auth.credentials.email) { "Null email" }

        val (handle, code) = requestHandleService.newRequestHandleWithCode(
            account,
            subject
        )

        val html = mimeHelper.getTemplate(
            template  = "mime-a2f",
            "code" to code
        )

        emailService.sendMimeEmail(
            subject = "Two-factor Authentication",
            to = email,
            html
        )

        return handle
    }

    @Transactional
    fun fulfillA2FRequest(dto: FulfillA2FRequestDto) {
        val account = requestHandleService.getAccountAndDiscardCheckedHandle(
            uuid = dto.requestHandle,
            subject,
            expirationTime = a2FRequestExpiresIn
        )

        tokenManagerService.signRefreshToken(account)

    }

    //Return type Nothing could not be used because it generated problem in tests.
    fun requireA2F(account: Account) {
        val handle = openA2FRequest(account)
        val url = getUrl(handle)

        throw A2FRequiredException(url)
    }

}