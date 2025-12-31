package com.yourRPG.chatPG.service.ai.providers.chutes

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.ai.IResponsive
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.chutes.model.ChutesResponse
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class ChutesAiService: IResponsive {

    private val client: HttpClient = HttpClient.newHttpClient() //FIXME

    private val objectMapper = ObjectMapper()

    private companion object val apiKey: String = System.getenv("CHUTES_API_KEY")

    override fun askAi(model: AiModel, prompt: String): String {

        val messageJson: String = getMessageJson(prompt)

        val request: HttpRequest = requestFromLlm(model, messageJson)

        try {
            val response: String =
                client.send(request, HttpResponse.BodyHandlers.ofString()).body()

            val chutesResponse: ChutesResponse =
                objectMapper.readValue(response, ChutesResponse::class.java)

            return chutesResponse.choices[0].message.content
        } catch (ex: MismatchedInputException) {
            throw UnavailableAiException(
                "An error occurred with the model ${model.getNickName()}: ${ex.message}"
            )
        } catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        } catch (e: InterruptedException) {
            throw java.lang.RuntimeException(e)
        }

    }

    private fun getMessageJson(prompt: String): String {
        val map = HashMap<String, String>()
        map["role"] = "system"
        map["content"] = prompt

        try {
            return objectMapper.writeValueAsString(map)
        } catch (ex: JsonProcessingException) {
            throw RuntimeException(ex)
        }
    }

    private fun requestFromLlm(model: AiModel, messageJson: String): HttpRequest {
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
                    "model": "${model.getModelName()}",
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