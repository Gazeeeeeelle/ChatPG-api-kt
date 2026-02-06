package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.FulfillPasswordChangeDto
import com.yourRPG.chatPG.dto.auth.OpenAccountCreationDto
import com.yourRPG.chatPG.dto.auth.OpenPasswordChangeDto
import com.yourRPG.chatPG.security.token.TokenManagerService
import org.springframework.stereotype.Service

/**
 * This service is an orchestrator to enhance code maintainability and organization. Please do not interact with Auth related
 *  services without first delegating to this service.
 */
@Service
class AuthService(
    private val authChangePasswordService: AuthChangePasswordService,
    private val authCreateAccountService: AuthCreateAccountService,
    private val authLogInOutService: AuthLogInOutService,
    private val tokenManagerService: TokenManagerService
) {

    /**
     * Delegates.
     * @see AuthLogInOutService.login
     */
    fun login(credentials: LoginCredentials): Pair<TokenDto, String> =
        authLogInOutService.login(credentials)

    /**
     * Delegates.
     * @see AuthLogInOutService.logout
     */
    fun logout(accountId: Long) {
        authLogInOutService.logout(accountId)
    }

    /**
     * Delegates.
     * @see AuthChangePasswordService.openPasswordChange
     */
    fun openPasswordChange(dto: OpenPasswordChangeDto) =
        authChangePasswordService.openPasswordChange(dto)

    /**
     * Delegates.
     * @see AuthChangePasswordService.confirmChangePassword
     */
    fun fulfillPasswordChange(dto: FulfillPasswordChangeDto) =
        authChangePasswordService.fulfillPasswordChange(dto)

    /**
     * Delegates.
     * @see AuthCreateAccountService.openAccountCreation
     */
    fun openAccountCreation(dto: OpenAccountCreationDto): AccountDto =
        authCreateAccountService.openAccountCreation(dto)

    /**
     * Delegates.
     * @see AuthCreateAccountService.fulfillAccountCreation
     */
    fun fulfillAccountCreation(dto: UuidDto): AccountDto =
        authCreateAccountService.fulfillAccountCreation(dto)

    /**
     * Delegates.
     * @see TokenManagerService.refreshTokens
     */
    fun refreshToken(oldRefreshToken: String): Pair<TokenDto, String> =
        tokenManagerService.refreshTokens(oldRefreshToken)

    /**
     * Delegates.
     * @see TokenManagerService.requireRefreshToken
     */
    fun requireRefreshToken(accountId: Long): Pair<TokenDto, String> =
        tokenManagerService.requireRefreshToken(accountId)

}