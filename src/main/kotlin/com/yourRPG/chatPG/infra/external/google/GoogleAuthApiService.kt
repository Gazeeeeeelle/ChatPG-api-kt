package com.yourRPG.chatPG.infra.external.google

import com.auth0.jwt.JWT
import com.yourRPG.chatPG.dto.external.google.GoogleAccessTokenDto
import com.yourRPG.chatPG.exception.http.BadRequestException
import com.yourRPG.chatPG.exception.http.ForbiddenException
import com.yourRPG.chatPG.exception.http.InternalServerException
import com.yourRPG.chatPG.exception.http.NotFoundException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.infra.uri.BackendUriHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Service
class GoogleAuthApiService(
    private val backendUriHelper: BackendUriHelper,

    private val restClient: RestClient,

    @param:Value("\${security.auth.external.google.client.id}")
    private val clientId: String,

    @param:Value("\${security.auth.external.google.client.secret}")
    private val clientSecret: String,
) {

    companion object {
        private const val GOOGLE_ACCOUNTS_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val GOOGLE_OAUTH_URL = "https://oauth2.googleapis.com"
        private const val GOOGLE_APIS_AUTH_URL  = "https://www.googleapis.com/auth"

        private const val CALLBACK_URI_PATH = "/auth/login/with/google/authorized"
    }

    /**
     * Returns the [GOOGLE_ACCOUNTS_AUTH_URL] used to redirect the user to the Google's OAuth page.
     *
     * @return URL to Google's OAuth page.
     */
    fun getCodeUrl(): String =
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
            .onStatus({ it.isError }) { _, response -> tokenResponseHandler(response) }
            .body(GoogleAccessTokenDto::class.java)
            ?.idToken
            ?: throw InternalServerException("Response from Github had invalid body")
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
    fun getEmail(code: String): String {
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
    fun buildTokenRequestBody(code: String) =
        LinkedMultiValueMap<String, String>().apply {
            add("code"         , code)
            add("client_id"    , clientId)
            add("client_secret", clientSecret)
            add("redirect_uri" , backendUriHelper.appendString(CALLBACK_URI_PATH))
            add("grant_type"   , "authorization_code")
        }

    /**
     * TODO: introduce a mapper that is able to take either HttpStatus enum or numerical code and return the respective
     *  Exception
     */
    fun tokenResponseHandler(response: ClientHttpResponse) {
        val code = response.statusCode
        val message = "Google: $code"
        when (response.statusCode) {
            HttpStatus.BAD_REQUEST ->
                throw BadRequestException("$message. Malformed token or request.")
            HttpStatus.UNAUTHORIZED ->
                throw UnauthorizedException("$message. Invalid or expired token sent to Google.")
            HttpStatus.FORBIDDEN ->
                throw ForbiddenException("$message. Rate limit exceeded or insufficient scopes.")
            HttpStatus.NOT_FOUND ->
                throw NotFoundException("$message. User emails endpoint not found.")
            else -> throw InternalServerException("Could not retrieve emails from Google's endpoint.")
        }
    }

}
