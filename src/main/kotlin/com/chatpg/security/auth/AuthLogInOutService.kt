package com.chatpg.security.auth

import com.chatpg.domain.account.Account
import com.chatpg.dto.auth.LoginCredentials
import com.chatpg.dto.auth.UuidDto
import com.chatpg.exception.http.sc4xx.NotFoundException
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.security.requesthandle.RequestHandleSubject
import com.chatpg.security.token.AccessAndRefreshTokens
import com.chatpg.security.token.TokenManagerService
import com.chatpg.service.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class AuthLogInOutService(
    private val passwordEncoder: PasswordEncoder,
    private val tokenManagerService: TokenManagerService,
    private val accountService: AccountService,
    private val authA2FService: AuthA2fService,
    private val requestHandleService: RequestHandleService,

    @param:Value($$"${security.request-handle.login-with-handle-expires-in}")
    private val loginWithHandleExpiresIn: Duration
) {

    private companion object {
        val log = KotlinLogging.logger {}

        const val DUMMY_ENCODED_PASSWORD = "$2a$10$58r.Gqy1ncdfGOkov08MguLtb/G18l5wcE2BTze1wx4JPUHGYtfFC"
    }

    //All outcomes that change the DB's state happen at the end of the execution paths.
    //Therefore, not marked with @Transactional
    fun login(credentials: LoginCredentials): AccessAndRefreshTokens {
        val account: Account? =
            try {
                accountService.getByName(credentials.username)
            } catch (_: NotFoundException) {
                log.warn { "Account not found for login" }
                null
            }

        //Performs the PasswordEncoder.matches not to give away discrepancies between non-existent account and wrong
        // password
        val passwordMatches = passwordEncoder.matches(
            credentials.password,
            account?.password ?: DUMMY_ENCODED_PASSWORD
        )

        if (!passwordMatches || account == null) {
            if (!passwordMatches) log.warn { "Wrong password" }
            throw UnauthorizedException("Invalid credentials",)
        }

        if (account.auth.a2f) {
            authA2FService.requireA2f(account)//Always throws A2FRequiredException
        }

        val tokens = tokenManagerService.signAccessAndRefreshTokens(account)
        log.info { "Successful login" }
        return tokens
    }

    @Transactional
    fun loginWithHandle(uuidDto: UuidDto): AccessAndRefreshTokens {
        log.info { "Logging in with handle..." }
        val account = requestHandleService.getAccountAndDiscardCheckedHandle(
            uuidDto.uuid,
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