package com.yourRPG.chatPG.security.auth.external

import com.yourRPG.chatPG.infra.external.auth.IAuthApiService
import com.yourRPG.chatPG.infra.external.auth.github.GithubAuthApiService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.service.account.AccountService
import java.util.UUID

abstract class ExternalLoginService(
    private val authApiService: IAuthApiService,
    private val accountService: AccountService,
    private val requestHandleService: RequestHandleService
) {

    /**
     * @see IAuthApiService.getCodeUrl
     */
    fun getCodeUrl(): String =
        authApiService.getCodeUrl()

    /**
     * Delegates to [GithubAuthApiService.getEmail] to find a valid email in the account owning the [code].
     *
     * @param code used to get its owner's email.
     * @return Access Token signed in the account's name.
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException if the email found did not correspond to an
     *  existing account.
     * @see GithubAuthApiService.getEmail
     * @see AccountService.getByEmail
     */
    fun loginWithCode(code: String): UUID {
        val email = authApiService.getEmail(code)

        val account = accountService.getByEmail(email)

        return requestHandleService.newRequestHandle(account, RequestHandleSubject.EXTERNAL_LOGIN)
    }

}