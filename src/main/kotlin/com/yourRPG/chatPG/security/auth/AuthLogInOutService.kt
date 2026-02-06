package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.exception.http.NotFoundException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthLogInOutService(
    private val passwordEncoder: PasswordEncoder,
    private val tokenManagerService: TokenManagerService,
    private val accountService: AccountService,
    private val authA2FService: AuthA2FService,
) {

    companion object {
        private const val DUMMY_ENCODED_PASSWORD = "$2a$10$58r.Gqy1ncdfGOkov08MguLtb/G18l5wcE2BTze1wx4JPUHGYtfFC"
    }

    //All outcomes that change the DB's state happen at the end of the method.
    fun login(credentials: LoginCredentials): Pair<TokenDto, String> {
        val account: Account? = try {
            accountService.getByName(credentials.username)
        } catch (_: NotFoundException) { //Masks status for security.
            null
        }

        //Performs the PasswordEncoder.matches not to give away discrepancies between non-existent account and wrong
        // password
        val passwordMatches = passwordEncoder.matches(
            credentials.password,
            account?.password ?: DUMMY_ENCODED_PASSWORD
        )

        if (!passwordMatches || account == null) {
            throw UnauthorizedException("Invalid credentials")
        }

        if (account.auth.a2f) authA2FService.requireA2F(account) //Always throws A2FRequiredException
        return tokenManagerService.requireRefreshToken(account)
    }

    fun logout(accountId: Long) =
        accountService.getById(accountId).let { account ->
            accountService.updateRefreshToken(account, refreshToken = null)
        }

}