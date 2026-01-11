package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class TokenFilter(
    private val tokenService: TokenService,
    private val accountService: AccountService
): Filter {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {

        if ((request as HttpServletRequest)
                .servletPath.startsWith("/login")
        ) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            val token = getToken(request)

            val accountId = tokenService.getClaim(token, "id").asLong()
            val account = accountService.getById(accountId)

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(account.id, null, account.authorities)

            filterChain.doFilter(request, response)
        }.onFailure { ex ->
            (response as HttpServletResponse)
                .sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.message)
        }
    }

   private fun getToken(request: HttpServletRequest): String {
        return request.getHeader("Authorization")
            ?.replace("Bearer ", "")
            ?: throw AccessToAccountUnauthorizedException("Authorization header is missing")
   }

}
