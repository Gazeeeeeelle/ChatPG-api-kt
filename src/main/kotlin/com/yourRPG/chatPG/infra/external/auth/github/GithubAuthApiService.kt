package com.yourRPG.chatPG.infra.external.auth.github

import com.yourRPG.chatPG.dto.external.github.GithubAccessTokenDto
import com.yourRPG.chatPG.dto.external.github.GithubEmailDto
import com.yourRPG.chatPG.exception.http.InternalServerException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.infra.external.auth.IAuthApiService
import com.yourRPG.chatPG.infra.external.auth.github.GithubAuthApiService.Companion.GITHUB_URL
import com.yourRPG.chatPG.infra.uri.BackendUriHelper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
) : IAuthApiService {

    private companion object {
        val log = KotlinLogging.logger {}

        const val GITHUB_URL = "https://github.com"
        const val GITHUB_API_URL = "https://api.github.com"
        const val CALLBACK_URI_PATH = "/auth/login/with/github/authorized"
    }

    override fun getLogger(): KLogger = log

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
    internal fun fetchToken(code: String): String =
        restClient.post()
            .uri("$GITHUB_URL/login/oauth/access_token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(buildTokenRequestBody(code))
            .retrieve()
            .validateResponse(clazz = GithubAccessTokenDto::class)
            .let(::validateGithubTokenResponse)

    internal fun fetchEmail(token: String): Array<GithubEmailDto> =
        restClient.get()
            .uri("$GITHUB_API_URL/user/emails")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .validateResponse(clazz = Array<GithubEmailDto>::class)

    /**
     * Returns the email address found by using the [code] to [fetchToken], and then sending it to the [GITHUB_URL] to see
     *  the account's emails.
     * Only uses the email address that is both primary and verified.
     *
     * @param code used to get the access token to the account.
     * @return [String] value of the email address.
     * @throws InternalServerException if the response, even after the status code and response treatments, failed to
     *  deserialize.
     * @see fetchToken
     */
    override fun getEmail(code: String): String {
        val token = fetchToken(code)

        val emails = fetchEmail(token)

        return emails.find { email -> email.primary && email.verified }
            ?.email
            ?: throw UnauthorizedException("No suitable email found between those registered in the account")
    }

    /**
     * Returns [GithubAccessTokenDto.accessToken] from [GithubAccessTokenDto].
     *  If an error is present in the [GithubAccessTokenDto], then [InternalServerException] is throw.
     *
     * @return Access Token in the [GithubAccessTokenDto].
     * @throws InternalServerException
     */
    internal fun validateGithubTokenResponse(dto: GithubAccessTokenDto): String {
        if (dto.errorDto.present) log.error {
            "Github answered with an error: ${dto.errorDto.error}, description: ${dto.errorDto.errorDescription}"
        }

        if (dto.accessToken == null) {
            log.error { "Null access token from Github's endpoint" }
            throw InternalServerException("Could not retrieve access token from Github's endpoint.")
        }

        return dto.accessToken
    }

    /**
     * Returns a [Map] that follows the JSON structure required for the request.
     *
     * @param code used to identify which request it aims to fulfill.
     * @return A name to value [Map] of the fields required.
     */
    internal fun buildTokenRequestBody(code: String) =
        LinkedMultiValueMap<String, String>().apply {
            add("code", code)
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("redirect_uri", backendUriHelper.appendString(CALLBACK_URI_PATH))
        }

}
