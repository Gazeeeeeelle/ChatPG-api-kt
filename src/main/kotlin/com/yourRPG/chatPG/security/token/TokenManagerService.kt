package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenManagerService(
    private val tokenService: TokenService,
    private val accountService: AccountService,
) {

    /**
     * Refresh both the *Access Token* and the *Refresh Token*. Returns them in the order they were mentioned in a [Pair].
     *
     * @param oldRefreshToken refresh token used to check authenticity of the request.
     * @see AccountService.getByRefreshToken
     * @see signAccessToken
     * @throws com.yourRPG.chatPG.exception.http.UnauthorizedException if the [oldRefreshToken] refresh token given did not identify
     *  an account.
     */
    @Transactional
    fun refreshTokens(oldRefreshToken: String): Pair<TokenDto, String> {
        tokenService.verify(oldRefreshToken)

        val account = accountService.getByRefreshToken(oldRefreshToken)

        val newAccessToken = signAccessToken(account)

        return TokenDto(newAccessToken) to newRefreshToken(account)
    }

    /**
     * Generates and signs a new access token.
     *
     * @param account token owner
     * @return JWT's String value
     * @see TokenService.signTokenWithLifetime
     */
    fun signAccessToken(account: Account): String =
        tokenService.signTokenWithLifetime(10L.minutes(), account)

    /**
     * Creates and signs a JWT token used as *Refresh Token*, which has longer lifetime, and is persisted in the [account]
     *  given.
     *
     * @param account where to persist the new token.
     * @return JWT's [String] value.
     * @see AccountService.saveWithRefreshToken
     */
    fun newRefreshToken(account: Account): String =
        tokenService.signTokenWithLifetime(7L.days(), account).apply {
            accountService.saveWithRefreshToken(account, refreshToken = this)
        }

    /**
     * Wrappers for [Duration]'s *"of"* methods to increase readability.
     */
    fun (Long).minutes(): Duration = Duration.ofMinutes(this)
    fun (Long).days()   : Duration = Duration.ofDays(this)

}