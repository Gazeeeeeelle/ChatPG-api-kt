package com.chatpg.infra.external.auth

import com.chatpg.exception.http.HttpException
import com.chatpg.exception.http.sc5xx.InternalServerException
import io.github.oshai.kotlinlogging.KLogger
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient
import kotlin.reflect.KClass

interface IAuthApiService {

    fun getLogger(): KLogger

    fun getCodeUrl(): String

    fun getEmail(code: String): String

    fun responseHandler(response: ClientHttpResponse): Boolean {
        val status = response.statusCode
        if (status.is2xxSuccessful) return true

        val stringBody = response.body
            .bufferedReader()
            .use { it.readText() }

        getLogger().error { "$status: $stringBody" }

        throw HttpException(status.value(), response.statusText)
    }

    /**
     * This extension function wraps the repetitive structure of applying [responseHandler] onto the
     *  received response and then deserializing its body to a given class.
     *
     * @param clazz Which class for the response's body to deserialize into.
     * @return Instance of [KClass] [clazz] given.
     * @throws InternalServerException if the deserialized object's reference was NULL, either because of failure or
     *  originally null value.
     */
    fun <T : Any> (RestClient.ResponseSpec).validateResponse(clazz: KClass<T>): T {
        onStatus({ it.isError }) { _, response -> responseHandler(response) }
        return body(clazz.java)
            ?: throw InternalServerException("Null from deserialization")
    }

}