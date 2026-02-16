package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenManagerService(
    private val tokenService: TokenService,
    private val accountService: AccountService,

    @param:Value("\${security.token.access-expires-in}")
    private val accessTokenExpiresIn: Duration,

    @param:Value("\${security.token.refresh-expires-in}")
    private val refreshTokenExpiresIn: Duration
) {

    /**
     * Refresh both the *Access Token* and the *Refresh Token*. Returns them in the order they were mentioned in a [Pair].
     *
     * @param oldRefreshToken refresh token used to check authenticity of the request.
     * @return [Pair] of [TokenDto], containing the access token, and the [String] value of the refresh token.
     * @throws com.yourRPG.chatPG.exception.http.UnauthorizedException if the [oldRefreshToken] refresh token given did not identify
     *  an account.
     * @see AccountService.getByRefreshToken
     * @see signAccessToken
     */
    @Transactional
    fun refreshTokens(oldRefreshToken: String): AccessAndRefreshTokens {
        tokenService.verify(oldRefreshToken)

        val account = accountService.getByRefreshToken(oldRefreshToken)

        return signAccessAndRefreshTokens(account)
    }

    @Transactional
    fun signAccessAndRefreshTokens(account: Account): AccessAndRefreshTokens =
        AccessAndRefreshTokens(signAccessToken(account), signRefreshToken(account))

    /**
     * Generates and signs a new access token.
     *
     * @param account token owner
     * @return JWT's String value
     * @see TokenService.signTokenWithLifetime
     */
    internal fun signAccessToken(account: Account): String =
        tokenService.signTokenWithLifetime(accessTokenExpiresIn, account)

    /**
     * Creates and signs a JWT token used as *Refresh Token*, which has longer lifetime, and is persisted in the [account]
     *  given.
     *
     * @param account where to persist the new token.
     * @return JWT's [String] value.
     * @see AccountService.updateRefreshToken
     */
    internal fun signRefreshToken(account: Account): String =
        tokenService.signTokenWithLifetime(refreshTokenExpiresIn, account).apply {
            accountService.updateRefreshToken(account, refreshToken = this)
        }

}