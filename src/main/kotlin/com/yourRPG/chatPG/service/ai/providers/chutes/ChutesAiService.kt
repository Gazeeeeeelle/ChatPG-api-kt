package com.yourRPG.chatPG.service.ai.providers.chutes

import com.fasterxml.jackson.databind.ObjectMapper
import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.ai.IResponsive
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiProvider
import com.yourRPG.chatPG.service.ai.providers.chutes.model.ChutesResponse
import com.yourRPG.chatPG.service.http.HttpService
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class ChutesAiService(
    private val httpService: HttpService
): IResponsive {

    override fun getProvider(): AiProvider = AiProvider.CHUTES

    private companion object {
        val objectMapper = ObjectMapper()
        val apiKey: String = System.getenv("CHUTES_API_KEY")
    }

    /**
     * @see IResponsive.askAi
     */
    override fun askAi(model: AiModel, prompt: String): String {

        val messageJson = getMessageJson(prompt)

        val response = buildRequestToLlm(model, messageJson).let { request ->
            httpService.send(request, HttpResponse.BodyHandlers.ofString()).body()
        }

        return runCatching {
            objectMapper.readValue(response, ChutesResponse::class.java)
        }.getOrElse {
            throw UnavailableAiException("The model ${model.nickname} is unavailable")
        }.choices.firstOrNull()?.message?.content
            ?: throw UnavailableAiException("Model ${model.nickname}'s response was empty")

    }

    private fun getMessageJson(prompt: String): String {
        val map = HashMap<String, String>()
        map["role"] = "system"
        map["content"] = prompt

        return objectMapper.writeValueAsString(map)
    }

    private fun buildRequestToLlm(model: AiModel, messageJson: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("https://llm.chutes.ai/v1/chat/completions"))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "Application/json")
            .POST(HttpRequest.BodyPublishers.ofString(getRequestJson(model, messageJson)))
            .build()
    }

    private fun getRequestJson(model: AiModel, messageJson: String): String {
        return  """
                {
                    "model": "${model.modelName}",
                    "messages": [
                        $messageJson
                    ],
                    "stream": false,
                    "max_tokens": 1024,
                    "temperature": 0.7
                }
                """
    }

}