package com.yourRPG.chatPG.service.ai.providers

import com.google.genai.Client
import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.ai.IResponsive
import org.springframework.stereotype.Service

@Service
class GoogleAiService: IResponsive {

    override fun getProvider(): AiProvider = AiProvider.GOOGLE

    private val client: Client = Client()

    /**
     * @see IResponsive.askAi
     *
     * Though, Google has the [com.google.genai.errors.ServerException] for when anything goes wrong on their side. And therefore, instead of
     *  catching the [com.fasterxml.jackson.databind.exc.MismatchedInputException], like it's usually done,
     *  the [com.google.genai.errors.ServerException] is caught to then throw the usual [com.yourRPG.chatPG.exception.ai.models.UnavailableAiException].
     */
    override fun askAi(model: AiModel, prompt: String): String? =
        runCatching {
            client.models.generateContent(
                model.modelName,
                prompt,
                null
            ).text()
        }.getOrElse {
            throw UnavailableAiException("The model ${model.nickname} is temporarily unavailable.")
        }

}