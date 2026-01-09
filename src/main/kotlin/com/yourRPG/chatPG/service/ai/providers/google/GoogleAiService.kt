package com.yourRPG.chatPG.service.ai.providers.google

import com.google.genai.Client
import com.google.genai.errors.ServerException
import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.ai.IResponsive
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiProvider
import org.springframework.stereotype.Service

@Service
class GoogleAiService: IResponsive {

    override fun getProvider(): AiProvider = AiProvider.GOOGLE

    private val client: Client = Client()

    /**
     * @see IResponsive.askAi
     *
     * Though, Google has the [ServerException] for when anything goes wrong on their side. And therefore, instead of
     *  catching the [com.fasterxml.jackson.databind.exc.MismatchedInputException], like it's usually done,
     *  the [ServerException] is caught to then throw the usual [UnavailableAiException].
     */
    override fun askAi(model: AiModel, prompt: String): String? {

        return runCatching {
            client.models.generateContent(
                model.modelName,
                prompt,
                null
            ).text()
        }.getOrElse {
            throw UnavailableAiException("The model ${model.nickname} is temporarily unavailable.")
        }

    }

}