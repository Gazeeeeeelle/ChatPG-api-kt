package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.LoginCredentials
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.exception.http.NotFoundException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.security.token.AccessAndRefreshTokens
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class AuthLogInOutService(
    private val passwordEncoder: PasswordEncoder,
    private val tokenManagerService: TokenManagerService,
    private val accountService: AccountService,
    private val authA2FService: AuthA2fService,
    private val requestHandleService: RequestHandleService,

    @param:Value("\${security.request-handle.login-with-handle-expires-in}")
    private val loginWithHandleExpiresIn: Duration
) {

    private companion object {
        val log = KotlinLogging.logger {}

        const val DUMMY_ENCODED_PASSWORD = "$2a$10$58r.Gqy1ncdfGOkov08MguLtb/G18l5wcE2BTze1wx4JPUHGYtfFC"
    }

    //All outcomes that change the DB's state happen at the end of the method.
    fun login(credentials: LoginCredentials): AccessAndRefreshTokens {
        val account: Account? =
            try {
                accountService.getByName(credentials.username)
            } catch (_: NotFoundException) { //Masks status for security.
                log.info { "Account not found for login" }
                null
            }

        //Performs the PasswordEncoder.matches not to give away discrepancies between non-existent account and wrong
        // password
        val passwordMatches = passwordEncoder.matches(
            credentials.password,
            account?.password ?: DUMMY_ENCODED_PASSWORD
        )

        if (!passwordMatches || account == null) {
            if (!passwordMatches) log.info { "Wrong password" } //FIXME
            throw UnauthorizedException("Invalid credentials")
        }

        if (account.auth.a2f) {
            authA2FService.requireA2f(account)//Always throws A2FRequiredException
        }

        val tokens = tokenManagerService.signAccessAndRefreshTokens(account)
        log.info { "Successful login" }
        return tokens
    }

    fun loginWithHandle(uuidDto: UuidDto): AccessAndRefreshTokens {
        log.info { "Logging in with handle..." }
        val account = requestHandleService.getAccountAndDiscardCheckedHandle(
            uuidDto.value,
            RequestHandleSubject.EXTERNAL_LOGIN,
            loginWithHandleExpiresIn
        )

        return tokenManagerService.signAccessAndRefreshTokens(account)
    }

    fun logout(accountId: Long) =
        accountService.getById(accountId).let { account ->
            accountService.updateRefreshToken(account, refreshToken = null)
        }

}