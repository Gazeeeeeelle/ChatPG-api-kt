package com.chatpg.security.auth

import com.chatpg.domain.account.Account
import com.chatpg.dto.auth.FulfillA2fDto
import com.chatpg.exception.auth.A2fRequiredException
import com.chatpg.infra.email.EmailService
import com.chatpg.infra.email.MimeHelper
import com.chatpg.infra.uri.FrontendUriHelper
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.security.requesthandle.RequestHandleSubject
import com.chatpg.security.token.AccessAndRefreshTokens
import com.chatpg.security.token.TokenManagerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*

@Service
class AuthA2fService(
    private val requestHandleService: RequestHandleService,
    private val emailService: EmailService,
    private val tokenManagerService: TokenManagerService,

    private val mimeHelper: MimeHelper,
    private val frontendUriHelper: FrontendUriHelper,

    @param:Value($$"${security.request-handle.a2f-expires-in}")
    private val a2FRequestExpiresIn: Duration,
) {

    companion object {
        private val subject = RequestHandleSubject.A2F
    }

    internal fun buildUrl(handle: UUID): String =
        frontendUriHelper.run {
            appendString("$a2fPath?uuid=$handle")
        }

    @Transactional
    internal fun openA2f(account: Account): UUID {
        val email = requireNotNull(account.auth.credentials.email) { "Null email" }

        val (handle, code) = requestHandleService.newRequestHandleWithCode(account, subject)

        sendA2FEmail(to = email, code)

        return handle
    }

    internal fun sendA2FEmail(to: String, code: String) {
        emailService.sendMimeEmail(
            subject = "Two-factor Authentication",
            to,
            html =
                mimeHelper.getTemplate(
                    template  = "mime-a2f",
                    "code" to code
                )
        )
    }

    @Transactional
    fun fulfillA2f(dto: FulfillA2fDto): AccessAndRefreshTokens {
        val account =
            requestHandleService.getAccountAndDiscardCheckedHandle(
                uuid = dto.requestHandle,
                subject,
                code = dto.code,
                expirationTime = a2FRequestExpiresIn
            )

        return tokenManagerService.signAccessAndRefreshTokens(account)
    }

    //Return type Nothing could not be used because it generated problem in tests.
    fun requireA2f(account: Account) {
        val handle = openA2f(account)
        val url = buildUrl(handle)

        throw A2fRequiredException(url)
    }

}
