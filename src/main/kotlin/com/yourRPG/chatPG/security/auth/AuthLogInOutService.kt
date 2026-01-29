package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.stereotype.Service

@Service
class AuthLogInOutService(
    private val authPasswordService: AuthPasswordService,
    private val tokenManagerService: TokenManagerService,
    private val accountService: AccountService,
) {

    fun login(credentials: LoginCredentials): Pair<TokenDto, String> {
        val account = accountService.getByName(credentials.username)

        authPasswordService.passwordEncoder.matches(credentials.password, account.password)
            .takeIf { it }
            ?: throw AccessToAccountUnauthorizedException("Wrong password")

        val token = tokenManagerService.signAccessToken(account)

        val refreshToken = tokenManagerService.newRefreshToken(account)

        return TokenDto(token) to refreshToken
    }

    fun logout(accountId: Long) =
        accountService.getById(accountId).let { account ->
            accountService.saveWithRefreshToken(account, refreshToken = null)
        }

}