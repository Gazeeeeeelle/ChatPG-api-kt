package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.account.ChangePasswordDto
import com.yourRPG.chatPG.dto.auth.account.CreateAccountDto
import com.yourRPG.chatPG.security.token.TokenManagerService
import jakarta.servlet.http.HttpServletResponse
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
    fun login(response: HttpServletResponse, credentials: LoginCredentials): TokenDto =
        authLogInOutService.login(response, credentials)

    /**
     * Delegates.
     * @see AuthLogInOutService.logout
     */
    fun logout(accountId: Long) {
        authLogInOutService.logout(accountId)
    }

    /**
     * Delegates.
     * @see AuthChangePasswordService.requestChangePassword
     */
    fun requestChangePassword(email: String) =
        authChangePasswordService.requestChangePassword(email)

    /**
     * Delegates.
     * @see AuthChangePasswordService.confirmChangePassword
     */
    fun confirmChangePassword(dto: ChangePasswordDto) =
        authChangePasswordService.confirmChangePassword(dto)

    /**
     * Delegates.
     * @see AuthCreateAccountService.createAccount
     */
    fun createAccount(dto: CreateAccountDto): AccountDto =
        authCreateAccountService.createAccount(dto)

    /**
     * Delegates.
     * @see AuthCreateAccountService.activateAccount
     */
    fun activateAccount(uuid: UuidDto): AccountDto =
        authCreateAccountService.activateAccount(uuid)

    /**
     * Delegates.
     * @see TokenManagerService.refreshTokens
     */
    fun refreshToken(response: HttpServletResponse, oldRefresh: String): TokenDto {
        return tokenManagerService.refreshTokens(response, oldRefresh)
    }

}