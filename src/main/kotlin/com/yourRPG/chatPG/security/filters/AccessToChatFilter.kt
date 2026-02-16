package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.exception.http.HttpException
import com.yourRPG.chatPG.security.helper.SecurityContextHelper
import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class AccessToChatFilter(
    private val validator: AccountHasAccessToChatValidator,
    private val securityContext: SecurityContextHelper
): OncePerRequestFilter() {

    private companion object {
        val log = KotlinLogging.logger {}

        const val GROUP_OF_UUID_CHARACTERS = "([-A-Fa-f0-9]+)"

        val captureChatPublicIdRegex = ApplicationEndpoints.Chat.BASE_BY_PUBLIC_CHAT_ID
            .replace(Regex(pattern = "\\{.*?}"), GROUP_OF_UUID_CHARACTERS)
    }

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val regex = Regex(pattern = captureChatPublicIdRegex)

        val path = request.servletPath
        val match: MatchResult? = regex.find(path)

        if (match != null) {
            try {
                val publicChatIdString = match.groupValues[1]
                val publicChatId = UUID.fromString(publicChatIdString)

                val accountId = securityContext.getPrincipal()

                validator.validate(t = accountId to publicChatId)
            } catch(ex: HttpException) {
                log.warn { "Forbidden attempt to access chat" }
                response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.message)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

}