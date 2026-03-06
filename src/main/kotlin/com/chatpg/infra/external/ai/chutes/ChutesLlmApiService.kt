package com.chatpg.infra.external.ai.chutes

import com.chatpg.dto.external.chutes.ChutesChoice
import com.chatpg.dto.external.chutes.ChutesResponse
import com.chatpg.exception.http.HttpException
import com.chatpg.exception.http.ServiceUnavailableException
import com.chatpg.logging.LoggingUtils
import com.chatpg.service.ai.providers.AiModel
import org.slf4j.event.Level
import org.springframework.beans.factory.annotation.Value
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

    private companion object {
        val log = LoggingUtils(this)
    }

    fun askAi(model: AiModel, prompt: String): String {
        val responseSpec = restClient.post()
            .uri(llmUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $apiKey")
            .body(getRequestJson(model, prompt))
            .retrieve()

        val responseBody: ChutesResponse = responseSpec
            .onStatus({ it.isError }) { _, response -> llmResponseHandler(response, model) }
            .body(ChutesResponse::class.java)
            ?: log.logAndThrowAt(Level.ERROR) {
                ServiceUnavailableException("Response from Chutes had invalid body")
            }

        val firstChoice: ChutesChoice = responseBody
            .choices
            .firstOrNull()
            ?: log.logAndThrowAt(Level.ERROR) {
                ServiceUnavailableException("No choices available")
            }

        return firstChoice
            .message
            .content
    }

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

    fun llmResponseHandler(response: ClientHttpResponse, model: AiModel) {
        val code = response.statusCode
        var codeValue = code.value()

        val level =
            if (arrayOf(400, 401, 403, 404).contains(codeValue)) {
                Level.WARN
            } else {
                codeValue = 500
                Level.ERROR
            }

        log.logAndThrow {
            HttpException(
                codeValue,
                "Chutes LLM(${model.nickname}): $code",
                level,
                "Response body: ${response.body}"
            )
        }
    }

}