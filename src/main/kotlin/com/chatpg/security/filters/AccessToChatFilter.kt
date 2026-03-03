package com.chatpg.security.filters

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.exception.http.HttpException
import com.chatpg.exception.http.UnauthorizedException
import com.chatpg.logging.LoggingUtils
import com.chatpg.security.helper.SecurityContextHelper
import com.chatpg.validator.chat.AccountHasAccessToChatValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.event.Level
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class AccessToChatFilter(
    private val validator: AccountHasAccessToChatValidator,
    private val securityContext: SecurityContextHelper
): OncePerRequestFilter() {

    private companion object {
        val log = LoggingUtils(this)

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
                    ?: log.logAndThrow {
                        UnauthorizedException(
                            "Null account ID",
                            level = Level.ERROR,
                            internalMessage = "Null Principal from Security Context."
                        )
                    }

                validator.validate(t = accountId to publicChatId)
            } catch(ex: HttpException) {
                log.at(Level.WARN) { "Forbidden attempt to access chat" }

                response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.message)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

}