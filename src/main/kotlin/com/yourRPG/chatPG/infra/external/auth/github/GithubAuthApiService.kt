package com.yourRPG.chatPG.infra.external.auth.github

import com.yourRPG.chatPG.dto.external.github.GithubAccessTokenDto
import com.yourRPG.chatPG.dto.external.github.GithubEmailDto
import com.yourRPG.chatPG.exception.http.*
import com.yourRPG.chatPG.infra.external.auth.IAuthApiService
import com.yourRPG.chatPG.infra.external.auth.github.GithubAuthApiService.Companion.GITHUB_URL
import com.yourRPG.chatPG.infra.uri.BackendUriHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Service
class GithubAuthApiService(
    private val backendUriHelper: BackendUriHelper,

    private val restClient: RestClient,

    @param:Value("\${security.auth.external.github.client.id}")
    private val clientId: String,

    @param:Value("\${security.auth.external.github.client.secret}")
    private val clientSecret: String,
): IAuthApiService {

    companion object {
        private const val GITHUB_URL = "https://github.com"
        private const val GITHUB_API_URL = "https://api.github.com"
        private const val CALLBACK_URI_PATH = "/auth/login/with/github/authorized"
        private val BAD_REQUEST_EXCEPTION =
            BadRequestException("GitHub: 400 - Bad Request. Invalid request sent to Github")
    }

    /**
     * Returns the [GITHUB_URL] used to redirect the user to the GitHub's OAuth page.
     *
     * @return URL to the OAuth GitHub page.
     */
    override fun getCodeUrl(): String =
        "$GITHUB_URL/login/oauth/authorize" +
                "?client_id=$clientId" +
                "&redirect_uri=${backendUriHelper.appendString(CALLBACK_URI_PATH)}" +
                "&scope=read:user,user:email"


    /**
     * Returns the [GITHUB_URL] used to redirect the user to the GitHub's OAuth page.
     *
     * @return URL to the OAuth GitHub page.
     */
    fun getToken(code: String): String {
        return restClient.post()
            .uri("$GITHUB_URL/login/oauth/access_token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(buildTokenRequestBody(code))
            .retrieve()
            .onStatus({ it.isError }) { _, response -> tokenResponseHandler(response) }
            .body(GithubAccessTokenDto::class.java)
            ?.let(::validateGithubTokenResponse)
            ?: throw ServiceUnavailableException("Response from Github had invalid body")
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
    override fun getEmail(code: String): String {
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
     * Handles status code of the response given by endpoint [/user/emails](http://github/user/emails). This endpoint's
     *  documentation resides [here](https://docs.github.com/en/rest/users/emails?apiVersion=2022-11-28).
     *
     * @param response [ClientHttpResponse] to be evaluated.
     * @return A [Boolean] to satisfy [RestClient.ResponseSpec.onStatus].
     * @throws InternalServerException if the response's status code is unhandled.
     */
    private fun emailResponseHandler(response: ClientHttpResponse): Boolean =
        when (response.statusCode) {
            HttpStatus.OK           -> true
            HttpStatus.BAD_REQUEST  -> throw BAD_REQUEST_EXCEPTION
            HttpStatus.UNAUTHORIZED -> throw UnauthorizedException("GitHub: 401 - Unauthorized.")
            HttpStatus.FORBIDDEN    -> throw ForbiddenException("GitHub: 403 - Forbidden.")
            HttpStatus.NOT_FOUND    -> throw NotFoundException("GitHub: 404 - Not Found.")
            else -> throw InternalServerException("Could not retrieve emails from Github's endpoint.")
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
     * Returns a [Map] that follows the JSON structure required for the request.
     *
     * @param code used to identify which request it aims to fulfill.
     * @return A name to value [Map] of the fields required.
     */
    fun buildTokenRequestBody(code: String) =
        LinkedMultiValueMap<String, String>().apply {
            add("code", code)
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("redirect_uri", backendUriHelper.appendString(CALLBACK_URI_PATH))
        }


    /**
     * Handles status code of the response given by endpoint [/login/oauth/access_token](http://github/user/emails/login/oauth/access_token). This endpoint's
     *  documentation resides [here](https://docs.github.com/en/apps/oauth-apps/maintaining-oauth-apps/troubleshooting-oauth-app-access-token-request-errors?apiVersion=2022-11-28).
     *
     * @param response [ClientHttpResponse] to be evaluated.
     * @return A [Boolean] to satisfy [RestClient.ResponseSpec.onStatus].
     * @throws HttpException of the respective status code.
     * @throws InternalServerException if the response's status code is unhandled.
     */
    private fun tokenResponseHandler(response: ClientHttpResponse): Unit =
        when (response.statusCode) {
            HttpStatus.BAD_REQUEST  -> throw BAD_REQUEST_EXCEPTION
            HttpStatus.UNAUTHORIZED -> throw UnauthorizedException("Github: 401 - Unauthorized. Invalid or expired token sent to Github.")
            HttpStatus.FORBIDDEN    -> throw ForbiddenException("Github: 403 - Forbidden. Rate limit exceeded or insufficient scopes.")
            HttpStatus.NOT_FOUND    -> throw NotFoundException("Github: 404 - Not Found. User emails endpoint not found.")
            else -> throw InternalServerException("Could not retrieve emails from Github's endpoint.")
        }

}
