package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.exception.http.HttpException
import com.yourRPG.chatPG.security.helper.SecurityContextHelper
import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AccessToChatFilter(
    private val validator: AccountHasAccessToChatValidator,
    private val securityContext: SecurityContextHelper
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath

        val regex = Regex("${ApplicationEndpoints.Chat.BASE}/(\\d+)")

        val match: MatchResult? = regex.find(path)

        if (match != null) {
            try {
                val chatId = match.groupValues[1].toLong()
                val accountId = securityContext.getPrincipal()

                validator.validate(t = accountId to chatId)
            } catch(ex: HttpException) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.message)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

}