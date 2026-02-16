package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.auth.*
import com.yourRPG.chatPG.security.token.AccessAndRefreshTokens
import com.yourRPG.chatPG.security.token.TokenManagerService
import org.springframework.stereotype.Service

/**
 * This service is an orchestrator to enhance code maintainability and organization. Please do not interact with Auth related
 *  services without first delegating to this service.
 */
@Service
class AuthService(
    private val authA2fService: AuthA2fService,
    private val authChangePasswordService: AuthChangePasswordService,
    private val authCreateAccountService: AuthCreateAccountService,
    private val authLogInOutService: AuthLogInOutService,
    private val tokenManagerService: TokenManagerService
) {

    /**
     * Delegates.
     * @see AuthLogInOutService.login
     */
    fun login(credentials: LoginCredentials): AccessAndRefreshTokens =
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
     * @see AuthChangePasswordService.fulfillPasswordChange
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
    fun refreshToken(oldRefreshToken: String): AccessAndRefreshTokens =
        tokenManagerService.refreshTokens(oldRefreshToken)

    /**
     * Delegates.
     * @see AuthA2fService.fulfillA2f
     */
    fun fulfillA2f(dto: FulfillA2fDto): AccessAndRefreshTokens =
        authA2fService.fulfillA2f(dto)

    /**
     * Delegates.
     * @see AuthLogInOutService.loginWithHandle
     */
    fun loginWithHandle(uuidDto: UuidDto): AccessAndRefreshTokens =
        authLogInOutService.loginWithHandle(uuidDto)

}