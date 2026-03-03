package com.chatpg.infra.external.auth.google

import com.auth0.jwt.JWT
import com.chatpg.dto.external.google.GoogleAccessTokenDto
import com.chatpg.exception.http.InternalServerException
import com.chatpg.infra.external.auth.IAuthApiService
import com.chatpg.infra.external.auth.google.GoogleAuthApiService.Companion.GOOGLE_ACCOUNTS_AUTH_URL
import com.chatpg.infra.external.auth.google.GoogleAuthApiService.Companion.GOOGLE_OAUTH_URL
import com.chatpg.infra.uri.BackendUriHelper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Service
class GoogleAuthApiService(
    private val backendUriHelper: BackendUriHelper,

    private val restClient: RestClient,

    @param:Value($$"${security.auth.external.google.client.id}")
    private val clientId: String,

    @param:Value($$"${security.auth.external.google.client.secret}")
    private val clientSecret: String,
): IAuthApiService {

    private companion object {
        val log = KotlinLogging.logger {}

        const val GOOGLE_ACCOUNTS_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
        const val GOOGLE_OAUTH_URL = "https://oauth2.googleapis.com"
        const val GOOGLE_APIS_AUTH_URL  = "https://www.googleapis.com/auth"

        const val CALLBACK_URI_PATH = "/auth/login/with/google/authorized"
    }

    override fun getLogger(): KLogger = log

    /**
     * Returns the [GOOGLE_ACCOUNTS_AUTH_URL] used to redirect the user to the Google's OAuth page.
     *
     * @return URL to Google's OAuth page.
     */
    override fun getCodeUrl(): String =
        GOOGLE_ACCOUNTS_AUTH_URL +
                "?client_id=$clientId" +
                "&redirect_uri=${backendUriHelper.appendString(CALLBACK_URI_PATH)}" +
                "&scope=$GOOGLE_APIS_AUTH_URL/userinfo.email" +
                "&response_type=code"

    /**
     * Returns token given by [GOOGLE_OAUTH_URL] using the code given by the Google's OAuth page after successful login.
     *
     * @return Access token that enables access to account's email addresses.
     * @see buildTokenRequestBody
     */
    fun getToken(code: String): String {
        return restClient.post()
            .uri("$GOOGLE_OAUTH_URL/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(buildTokenRequestBody(code))
            .retrieve()
            .validateResponse(GoogleAccessTokenDto::class)
            .validateToken()
    }

    /**
     * Returns the email address found within the token retrieved from [getToken] using the [code] given.
     *
     * @param code used to get the access token to the account.
     * @return [String] value of the email address.
     * @throws InternalServerException if the response, even after the status code and response treatments, failed to
     *  deserialize.
     * @see getToken
     */
    override fun getEmail(code: String): String {
        val token = getToken(code)

        val decoded = JWT.decode(token)

        return decoded.getClaim("email").asString()
    }

    /**
     * Returns a [Map] that follows the JSON structure required for the request.
     *
     * @param code used to identify which request it aims to fulfill.
     * @return A name to value [Map] of the fields required.
     */
    internal fun buildTokenRequestBody(code: String) =
        LinkedMultiValueMap<String, String>().apply {
            add("code"         , code)
            add("client_id"    , clientId)
            add("client_secret", clientSecret)
            add("redirect_uri" , backendUriHelper.appendString(CALLBACK_URI_PATH))
            add("grant_type"   , "authorization_code")
        }

    internal fun (GoogleAccessTokenDto).validateToken(): String =
        idToken ?: run {
            log.error { "Null idToken from deserialization" }
            throw InternalServerException("Null idToken from deserialization")
        }

}
