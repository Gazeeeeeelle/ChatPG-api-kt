package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.exception.http.NotFoundException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
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
import java.util.*

@Component
class TokenFilter(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val securityContext: SecurityContextHelper,
): Filter {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {
        val servletRequest  = request  as HttpServletRequest
        val servletResponse = response as HttpServletResponse

        val path = servletRequest.servletPath

        if (
            !path.contains(ApplicationEndpoints.AuthSecure.BASE)
            && path.startsWith(ApplicationEndpoints.Auth.BASE)
        ) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            val token = tokenService.getAccessToken(servletRequest)

            val publicId = tokenService.getClaim(token, "id").asString()
                ?.let { UUID.fromString(it) }
                ?: throw UnauthorizedException("Unauthorized")

            val account = accountService.getByPublicId(publicId)

            securityContext.setPrincipal(account.id, account.authorities)

            filterChain.doFilter(request, response)
        }.onFailure { ex ->
            val status = when (ex) {
                is UnauthorizedException -> HttpServletResponse.SC_UNAUTHORIZED
                is NotFoundException     -> HttpServletResponse.SC_UNAUTHORIZED //Masks status for security.
                else -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            }

            servletResponse.sendError(status, ex.message ?: "Unknown error")
        }

    }

}
