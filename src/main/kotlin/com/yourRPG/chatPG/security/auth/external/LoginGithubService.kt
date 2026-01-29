package com.yourRPG.chatPG.security.auth.external

import com.yourRPG.chatPG.dto.auth.login.with.github.GithubAccessTokenDto
import com.yourRPG.chatPG.dto.auth.login.with.github.GithubEmailDto
import com.yourRPG.chatPG.exception.http.*
import com.yourRPG.chatPG.helper.uri.BackendUriHelper
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Service
class LoginGithubService(
    private val tokenManagerService: TokenManagerService,
    private val accountService: AccountService,

    private val backendUriHelper: BackendUriHelper,

    @param:Value("\${security.auth.external.github.client.id}")
    private val clientId: String,

    @param:Value("\${security.auth.external.github.client.secret}")
    private val clientSecret: String,

    restClientBuilder: RestClient.Builder
) {

    companion object {
        private const val GITHUB_URL = "https://github.com"
        private const val GITHUB_API_URL = "https://api.github.com"
        private const val CALLBACK_URI_PATH = "/auth/login/with/github/authorized"
        private val BAD_REQUEST_EXCEPTION = BadRequestException("Invalid request sent to Github")
    }

    private val restClient = restClientBuilder.build()

    /**
     * Returns the [GITHUB_URL] used to redirect the user to the OAuth GitHub page.
     *
     * @return URL to the OAuth GitHub page.
     */
    fun getCodeUrl(): String =
        "$GITHUB_URL/login/oauth/authorize" +
                "?client_id=$clientId" +
                "&redirect_uri=${backendUriHelper.append(CALLBACK_URI_PATH)}" +
                "&scope=read:user,user:email"

    /**
     * Returns token given by [GITHUB_API_URL] using the code given by the GitHub's OAuth page after successful login.
     *
     * @return Access token that enables access to account's email addresses.
     * @see buildTokenRequestBody
     * @see tokenResponseHandler
     * @see GithubAccessTokenDto
     * @see validateGithubTokenResponse
     */
    fun getToken(code: String): String {
        return restClient.post()
            .uri("$GITHUB_URL/login/oauth/access_token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(buildTokenRequestBody(code))
            .retrieve()
            .onStatus(::tokenResponseHandler)
            .body(GithubAccessTokenDto::class.java)
            ?.let(::validateGithubTokenResponse)
            ?: throw InternalServerException("Response from Github had invalid body")
    }

    /**
     * Returns [GithubAccessTokenDto.accessToken] from [GithubAccessTokenDto].
     *  If an error is present in the [GithubAccessTokenDto], then [InternalServerException] is throw.
     *
     * @return Access Token in the [GithubAccessTokenDto].
     * @throws InternalServerException
     */
    fun validateGithubTokenResponse(dto: GithubAccessTokenDto): String {
        if (dto.errorDto.present) {
            throw InternalServerException(
                "Github responded with error ${dto.errorDto.error}, description: ${dto.errorDto.errorDescription}"
            )
        }
        return dto.accessToken ?: throw InternalServerException("Null access token from Github's endpoint")
    }

    /**
     * Delegates to [getEmail] to find a valid email in the account owning the [code].
     *
     * @param code used to get its owner's email.
     * @return Access Token signed in the account's name.
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException if the email found did not correspond to an
     *  existing account.
     * @see getEmail
     * @see AccountService.getByEmail
     */
    fun loginWithCode(code: String): String {
        val email = getEmail(code)

        val account = accountService.getByEmail(email)

        return tokenManagerService.signAccessToken(account)
    }

    /**
     * Returns the email address found by using the [code] to [getToken], and then sending it to the [GITHUB_URL] to see
     *  the account's emails.
     * Only uses the email address that is both primary and verified.
     *
     * @param code used to get the access token to the account.
     * @return [String] value of the email address.
     * @throws InternalServerException if the response, even after the status code and response treatments, failed to
     *  deserialize.
     * @see getToken
     * @see emailResponseHandler
     */
    fun getEmail(code: String): String {
        val token = getToken(code)

        val headers = HttpHeaders().apply {
            setBearerAuth(token)
        }

        val spec = restClient.get()
            .uri("$GITHUB_API_URL/user/emails")
            .headers { httpHeaders -> httpHeaders.addAll(headers) }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(this::emailResponseHandler)

        val emails = try {
            spec.body(Array<GithubEmailDto>::class.java)
                ?: throw InternalServerException("Null from deserialization")
        } catch(ex: InternalServerException) {
            throw InternalServerException("Failed to get email from Github's endpoint: ${ex.message}")
        }

        return emails.find { email -> email.primary && email.verified }
            ?.email
            ?: throw UnauthorizedException("No suitable email found between those registered in the account")
    }

    /**
     * Returns a [Map] that follows the JSON structure required for the request.
     *
     * @param code used to identify which request it aims to fulfill.
     * @return A name to value [Map] of the fields required.
     */
    fun buildTokenRequestBody(code: String) =
        LinkedMultiValueMap<String, String>() .apply {
            add("code", code)
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("redirect_uri", backendUriHelper.append(CALLBACK_URI_PATH))
        }

    /**
     * Handles status code of the response given by endpoint [/login/oauth/access_token](http://github/user/emails/login/oauth/access_token). This endpoint's
     *  documentation resides [here](https://docs.github.com/en/apps/oauth-apps/maintaining-oauth-apps/troubleshooting-oauth-app-access-token-request-errors?apiVersion=2022-11-28).
     *
     * @param response [ClientHttpResponse] to be evaluated.
     * @return A [Boolean] to satisfy [org.springframework.web.client.RestClient.ResponseSpec.onStatus].
     * @throws com.yourRPG.chatPG.exception.http.HttpException of the respective status code.
     * @throws InternalServerException if the response's status code is unhandled.
     */
    private fun tokenResponseHandler(response: ClientHttpResponse): Boolean =
        when (response.statusCode) {
            HttpStatus.OK           -> true
            HttpStatus.BAD_REQUEST  -> throw BAD_REQUEST_EXCEPTION
            HttpStatus.UNAUTHORIZED -> throw UnauthorizedException("Invalid or expired token sent to Github")
            HttpStatus.FORBIDDEN    -> throw ForbiddenException("Rate limit exceeded or insufficient scopes")
            HttpStatus.NOT_FOUND    -> throw NotFoundException("User emails endpoint not found")
            else -> throw InternalServerException("Could not retrieve emails from Github's endpoint")
        }

    /**
     * Handles status code of the response given by endpoint [/user/emails](http://github/user/emails). This endpoint's
     *  documentation resides [here](https://docs.github.com/en/rest/users/emails?apiVersion=2022-11-28).
     *
     * @param response [ClientHttpResponse] to be evaluated.
     * @return A [Boolean] to satisfy [org.springframework.web.client.RestClient.ResponseSpec.onStatus].
     * @throws InternalServerException if the response's status code is unhandled.
     */
    private fun emailResponseHandler(response: ClientHttpResponse): Boolean =
        when (response.statusCode) {
            HttpStatus.OK           -> true
            HttpStatus.BAD_REQUEST  -> throw BAD_REQUEST_EXCEPTION
            HttpStatus.UNAUTHORIZED -> throw UnauthorizedException("Github: 401 - Unauthorized.")
            HttpStatus.FORBIDDEN    -> throw ForbiddenException("Github: 403 - Forbidden.")
            HttpStatus.NOT_FOUND    -> throw NotFoundException("Github: 404 - Not Found.")
            else -> throw InternalServerException("Could not retrieve emails from Github's endpoint")
        }

}