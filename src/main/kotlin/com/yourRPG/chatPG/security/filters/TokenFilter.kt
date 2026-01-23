package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.security.helper.SecurityContextHelper
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class TokenFilter(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val securityContext: SecurityContextHelper
): Filter {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {
        val path = (request as HttpServletRequest)
            .servletPath

        if (path.contains("/auth") && !path.contains("/auth/logout")
        ) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            val token = tokenService.getAccessToken(request)

            val accountId = tokenService.getClaim(token, "id").asLong()
            val account = accountService.getById(accountId)

            securityContext.setPrincipal(accountId, account.authorities)
        }.onFailure { ex ->
            (response as HttpServletResponse)
                .sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.message)
        }

        filterChain.doFilter(request, response)
    }

}
