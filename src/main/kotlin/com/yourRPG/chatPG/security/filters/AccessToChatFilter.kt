package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AccessToChatFilter(
    private val validator: AccountHasAccessToChatValidator
): Filter {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {
        (request as HttpServletRequest)

        val path = request.servletPath

        val regex = Regex("/chats/(\\d+)/.*")

        val match: MatchResult? = regex.find(path)

        if (match != null) {
            runCatching {
                val chatId = match.groupValues[1].toLong()

                validator.validate(
                    t = SecurityContextHolder.getContext().authentication.principal
                            as Long
                    to chatId
                )
            }.onFailure { ex ->
                (response as HttpServletResponse)
                    .sendError(HttpServletResponse.SC_FORBIDDEN, ex.message)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

}