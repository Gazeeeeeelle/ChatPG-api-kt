package com.chatpg.security.filters

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.exception.account.AccountIdNotFoundException
import com.chatpg.exception.http.HttpException
import com.chatpg.exception.http.sc4xx.BadRequestException
import com.chatpg.logging.LoggingUtils
import com.chatpg.security.config.SwaggerDocumentationSecurityConfigurer
import com.chatpg.security.helper.SecurityContextHelper
import com.chatpg.security.token.TokenService
import com.chatpg.service.account.AccountService
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

    private val swaggerDocSecurityConfigurer: SwaggerDocumentationSecurityConfigurer,
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

            val claim: String? = tokenService.getClaim(token, "id")
                ?.asString()

            val publicId = parseUuidElseThrowBadRequestException(uuidString = claim.toString())

            val account = accountService.getByPublicId(publicId)
            val accountId = account.id
                ?: log.logAndThrow {
                    AccountIdNotFoundException()
                }

            securityContext.setPrincipal(accountId, account.authorities)

            filterChain.doFilter(request, response)
        } catch (ex: HttpException) {
            log.exception(loggableException = ex)

            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
            )
        }
    }

    /**
     * Parses [String] given into a UUID.
     *
     * @param uuidString [String] to be parsed.
     * @return [UUID] parsed UUID.
     * @throws BadRequestException if [String] given could not be parsed into a UUID.
     */
    internal fun parseUuidElseThrowBadRequestException(uuidString: String): UUID =
        try {
            UUID.fromString(uuidString)
        } catch (_: IllegalArgumentException) {
            throw BadRequestException("Malformed UUID")
        }

    /**
     * Returns a [Boolean] on whether the path should be excluded from authentication filter or not.
     *
     * @param path Path trying to be accessed.
     * @return True if no authentication is needed for the endpoint trying to be accessed.
     */
    internal fun excludePathFromAuthentication(path: String): Boolean =
        isPublicAuthRelated(path)
                || swaggerDocSecurityConfigurer.isPathSwaggerRelated(path)

    /**
     * Returns a [Boolean] on whether the path is included in [ApplicationEndpoints.Auth], but not in
     *  [ApplicationEndpoints.AuthSecure].
     *
     * @param path Path to be evaluated.
     * @return True if path is included in [ApplicationEndpoints.Auth] but not in [ApplicationEndpoints.AuthSecure].
     */
    internal fun isPublicAuthRelated(path: String): Boolean =
        !path.startsWith(ApplicationEndpoints.AuthSecure.BASE)
                && path.startsWith(ApplicationEndpoints.Auth.BASE)

}
