package com.chatpg.infra.external.ai.chutes

import com.chatpg.dto.external.chutes.ChutesResponse
import com.chatpg.exception.http.BadRequestException
import com.chatpg.exception.http.ForbiddenException
import com.chatpg.exception.http.InternalServerException
import com.chatpg.exception.http.NotFoundException
import com.chatpg.exception.http.ServiceUnavailableException
import com.chatpg.exception.http.UnauthorizedException
import com.chatpg.service.ai.providers.AiModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class ChutesLlmApiService(
    @param:Value($$"${spring.ai.chutes.llm.api-key}")
    private val apiKey: String,

    @param:Value($$"${spring.ai.chutes.llm.url}")
    private val llmUrl: String,

    private val restClient: RestClient
) {

    fun askAi(model: AiModel, prompt: String): String =
        restClient.post()
            .uri(llmUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $apiKey")
            .body(getRequestJson(model, prompt))
            .retrieve()
            .onStatus({ it.isError }) { _, response -> llmResponseHandler(response) }
            .body(ChutesResponse::class.java)
            ?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?: throw ServiceUnavailableException("Response from Chutes had invalid body")

    /**
     * Returns the *JSON* [String] containing all necessary information about
     *
     * @param model LLM to request from.
     * @param prompt treated prompt that was given by the caller.
     * @return [String] *JSON* of the request body.
     */
    private fun getRequestJson(model: AiModel, prompt: String): Map<String, *> =
        mapOf(
            "model"    to model.modelName,
            "messages" to listOf(
                mapOf(
                    "role"    to "system",
                    "content" to  prompt
                )
            ),
            "stream"      to false,
            "max_tokens"  to 1024,
            "temperature" to 0.7
        )

    fun llmResponseHandler(response: ClientHttpResponse): Unit =
        when(val code = response.statusCode) {
            HttpStatus.BAD_REQUEST  -> throw BadRequestException("Chutes: ${code}.")
            HttpStatus.UNAUTHORIZED -> throw UnauthorizedException("Chutes: ${code}.",)
            HttpStatus.FORBIDDEN    -> throw ForbiddenException("Chutes: ${code}.")
            HttpStatus.NOT_FOUND    -> throw NotFoundException("Chutes: ${code}.")
            HttpStatus.PAYMENT_REQUIRED -> throw ServiceUnavailableException("This model is not currently available.")
            else -> throw InternalServerException("Unhandled response from Chutes.")
        }

}