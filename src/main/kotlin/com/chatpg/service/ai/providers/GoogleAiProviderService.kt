package com.chatpg.service.ai.providers

import com.chatpg.exception.http.HttpException
import com.chatpg.logging.LoggingUtils
import com.chatpg.service.ai.IAiProviderService
import com.google.genai.Client
import org.slf4j.event.Level
import org.springframework.ai.retry.NonTransientAiException
import org.springframework.ai.retry.TransientAiException
import org.springframework.stereotype.Service

@Service
class GoogleAiProviderService: IAiProviderService {

    private companion object {
        val log = LoggingUtils(this)

        const val GENERIC_FAILURE_EXTERNAL_MESSAGE = "Something went wrong. Try again later."
    }

    override fun getProvider(): AiProvider = AiProvider.GOOGLE

    private final val client: Client = Client()

    /**
     * TODO
     *
     * @see IAiProviderService.askAi
     */
    override fun askAi(model: AiModel, prompt: String): String? =
        try {
            client
                .models
                .generateContent(
                    model.modelName,
                    prompt,
                    null
                )
                .text()
        } catch (ex: NonTransientAiException) {
            log.logAndThrow {
                HttpException(
                    503,
                    GENERIC_FAILURE_EXTERNAL_MESSAGE,
                    Level.ERROR,
                    "Google AI: ${ex.message}"
                )
            }
        } catch (ex: TransientAiException) {
            log.logAndThrow {
                HttpException(
                    503,
                    GENERIC_FAILURE_EXTERNAL_MESSAGE,
                    Level.WARN,
                    "Google AI: ${ex.message}"
                )
            }
        }

}