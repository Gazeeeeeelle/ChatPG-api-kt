package com.chatpg.security.filters

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.exception.account.AccountIdNotFoundException
import com.chatpg.exception.http.BadRequestException
import com.chatpg.exception.http.HttpException
import com.chatpg.logging.LoggingUtils
import com.chatpg.security.helper.SecurityContextHelper
import com.chatpg.security.token.TokenService
import com.chatpg.service.account.AccountService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.event.Level
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class TokenFilter(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val securityContext: SecurityContextHelper,
): OncePerRequestFilter() {

     private companion object {
         val log = LoggingUtils(this)
     }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath

        if (excludePathFromAuthentication(path)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = tokenService.getAccessToken(request)

            val claim: String? = tokenService.getClaim(token, "id")?.asString()

            val publicId = try {
                UUID.fromString(claim.toString())
            } catch (_: IllegalArgumentException) {
                throw BadRequestException("Malformed UUID")
            }

            val account = accountService.getByPublicId(publicId)
            val accountId = account.id
                ?: log.logAndThrow {
                    AccountIdNotFoundException()
                }

            securityContext.setPrincipal(accountId, account.authorities)

            filterChain.doFilter(request, response)
        } catch (ex: HttpException) {
            if (ex is BadRequestException) log.at(Level.WARN) { "BadRequest: ${ex.message}" }
            else log.at(Level.WARN) { "HttpException: ${ex.message}" }

            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED, //Masks status for security.
                "Unauthorized"
            )
        }

    }

    internal fun excludePathFromAuthentication(path: String): Boolean =
        !path.startsWith(ApplicationEndpoints.AuthSecure.BASE)
                && path.startsWith(ApplicationEndpoints.Auth.BASE)

}
