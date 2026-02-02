package com.yourRPG.chatPG.security.auth.external

import com.yourRPG.chatPG.infra.external.google.GoogleAuthApiService
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.stereotype.Service

@Service
class GoogleLoginService(
    private val tokenManagerService: TokenManagerService,
    private val accountService: AccountService,

    private val googleAuthApiService: GoogleAuthApiService
): ExternalLoginService {

    /**
     * @see GoogleAuthApiService.getCodeUrl
     */
    override fun getCodeUrl(): String = googleAuthApiService.getCodeUrl()

    /**
     * Delegates to [GoogleAuthApiService.getEmail] to find a valid email in the account owning the [code].
     *
     * @param code used to get its owner's email.
     * @return Access Token signed in the account's name.
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException if the email found did not correspond to an
     *  existing account.
     * @see GoogleAuthApiService.getEmail
     * @see AccountService.getByEmail
     */
    override fun loginWithCode(code: String): String {
        val email = googleAuthApiService.getEmail(code)

        val account = accountService.getByEmail(email)

        return tokenManagerService.signAccessToken(account)
    }

}