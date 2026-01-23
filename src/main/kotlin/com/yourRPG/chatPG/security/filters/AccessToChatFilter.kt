package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.security.helper.SecurityContextHelper
import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class AccessToChatFilter(
    private val validator: AccountHasAccessToChatValidator,
    private val securityContext: SecurityContextHelper
): Filter {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {
        (request as HttpServletRequest)

        val path = request.servletPath

        val regex = Regex(".*/chats/(\\d+)/.*")

        val match: MatchResult? = regex.find(path)

        if (match != null) {
            runCatching {
                val chatId = match.groupValues[1].toLong()
                val accountId = securityContext.getPrincipal() //DELEGATED

                validator.validate(t = accountId to chatId) //DELEGATED
            }.onFailure { ex ->
                (response as HttpServletResponse)
                    .sendError(HttpServletResponse.SC_FORBIDDEN, ex.message) //DELEGATED
                return
            }
        }

        filterChain.doFilter(request, response) //DELEGATED
    }

}