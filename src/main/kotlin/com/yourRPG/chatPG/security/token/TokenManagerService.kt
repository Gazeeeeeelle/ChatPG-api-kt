package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.auth.TokenDto
import com.yourRPG.chatPG.helper.http.CookieService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenManagerService(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val cookieService: CookieService
) {

    /**
     * Refresh both the *Access Token*, returned inside TokenDto, and the *Refresh Token*, put as a cookie in the [HttpServletResponse]
     *  object given.
     *
     * @param response where to add the cookie.
     * @param oldRefreshToken refresh token used to check authenticity of the request.
     * @see AccountService.getByRefreshToken
     * @see appendNewRefreshToken
     * @throws com.yourRPG.chatPG.exception.UnauthorizedException if the [oldRefreshToken] refresh token given did not identify
     *  an account.
     */
    @Transactional
    fun refreshTokens(response: HttpServletResponse, oldRefreshToken: String): TokenDto {
        tokenService.verify(oldRefreshToken)

        val account = accountService.getByRefreshToken(oldRefreshToken)

        val newAccessToken = signAccessToken(account)

        appendNewRefreshToken(response, owner = account)

        return TokenDto(newAccessToken)
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
     * Adds a cookie with name *"refresh_token"* to the response. Its value is of the [newRefreshToken] generated with
     *  owner [owner].
     *
     * @param response where to add the cookie.
     * @param owner owner of the generated refresh token.
     * @see CookieService.appendCookie
     */
    fun appendNewRefreshToken(response: HttpServletResponse, owner: Account) {
        cookieService.appendCookie(response,
            name  = "refresh_token",
            value = newRefreshToken(owner)
        )
    }

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