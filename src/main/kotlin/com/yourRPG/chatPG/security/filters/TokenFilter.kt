package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.security.token.TokenService
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
    private val accountRepository: AccountRepository
): Filter {

    private val ignoredPaths: List<String> =
        listOf("/login", "/accounts/exists")

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {
        (request as HttpServletRequest)

        if (ignoredPaths.contains(request.servletPath)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = getToken(request)

            val id = tokenService.getClaim(token, "id")
                .asLong()

            val account: Account = accountRepository.findById(id)
                .orElse(null) ?: throw AccountNotFoundException("Account not found")

            val authentication =
                UsernamePasswordAuthenticationToken(id, null, account.authorities)

            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
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