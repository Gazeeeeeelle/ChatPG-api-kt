package com.yourRPG.chatPG.service.ai.providers.chutes

import com.fasterxml.jackson.databind.ObjectMapper
import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.ai.IResponsive
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiProvider
import com.yourRPG.chatPG.service.ai.providers.chutes.model.ChutesResponse
import com.yourRPG.chatPG.helper.http.HttpService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class ChutesAiService(
    /* Services */
    private val httpService: HttpService,

    @param:Value("\${spring.ai.chutes.llm.api-key}")
    private val apiKey: String,

    @param:Value("\${spring.ai.chutes.llm.url}")
    private val llmUrl: String

): IResponsive {

    override fun getProvider(): AiProvider = AiProvider.CHUTES

    val objectMapper = ObjectMapper()

    /**
     * @see IResponsive.askAi
     */
    override fun askAi(model: AiModel, prompt: String): String {

        val response = buildRequestToLlm(model, prompt).let { request ->
            httpService.send(request, HttpResponse.BodyHandlers.ofString()).body()
        }

        return runCatching {
            objectMapper.readValue(response, ChutesResponse::class.java)
        }.getOrElse {
            throw UnavailableAiException("The model ${model.nickname} is unavailable")
        }.choices.firstOrNull()?.message?.content
            ?: throw UnavailableAiException("Model ${model.nickname}'s response was empty")

    }

    /**
     * Builds the **POST** [HttpRequest] inserting the *Authorization* and *Content-Type* headers, together with its
     *  body.
     *
     * @param model LLM to request from.
     * @param messageJson [String] *JSON* of the request body.
     * @return Built [HttpRequest]
     */
    private fun buildRequestToLlm(model: AiModel, messageJson: String): HttpRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(llmUrl))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "Application/json")
            .POST(HttpRequest.BodyPublishers.ofString(getRequestJson(model, messageJson)))
            .build()

    /**
     * Returns the *JSON* [String] containing all necessary information about
     *
     * @param model LLM to request from.
     * @param prompt treated prompt that was given by the caller.
     * @return [String] *JSON* of the request body.
     */
    private fun getRequestJson(model: AiModel, prompt: String): String =
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
        ).let { raw -> objectMapper.writeValueAsString(raw) }

}