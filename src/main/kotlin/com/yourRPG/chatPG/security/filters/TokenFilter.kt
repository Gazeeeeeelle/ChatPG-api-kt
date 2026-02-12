package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.exception.http.BadRequestException
import com.yourRPG.chatPG.exception.http.HttpException
import com.yourRPG.chatPG.security.helper.SecurityContextHelper
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.service.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class TokenFilter(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val securityContext: SecurityContextHelper,
): OncePerRequestFilter() {

    private companion object val log = KotlinLogging.logger {}

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

            securityContext.setPrincipal(account.id, account.authorities)

            filterChain.doFilter(request, response)
        } catch (ex: HttpException) {

            if (ex is BadRequestException) log.warn { "BadRequest: ${ex.message}" }
            else log.info { "HttpException: ${ex.message}" }

            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED, //Masks status for security.
                "Unauthorized"
            )
        } catch (ex: Exception) {
            log.error(ex) { "TokenFilter failure: ${ex.message}" }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown error")
        }

    }

    internal fun excludePathFromAuthentication(path: String): Boolean {
        return !path.startsWith(ApplicationEndpoints.AuthSecure.BASE)
                && path.startsWith(ApplicationEndpoints.Auth.BASE)
    }

}
