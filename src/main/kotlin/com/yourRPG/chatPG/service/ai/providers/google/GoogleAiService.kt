package com.yourRPG.chatPG.service.ai.providers.google

import com.google.genai.Client
import com.google.genai.errors.ServerException
import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.ai.IResponsive
import com.yourRPG.chatPG.service.ai.providers.AiModel
import org.springframework.stereotype.Service


@Service
class GoogleAiService: IResponsive {

    private val client: Client = Client()

    /**
     * @see IResponsive.askAi
     */
    override fun askAi(model: AiModel, prompt: String): String? {
        try {
            val response =
                client.models.generateContent(
                    model.getModelName(),
                    prompt,
                    null
                )
            return response.text()
        } catch (ex: ServerException) {
            throw UnavailableAiException(
                "An error occurred with the model " +
                        model.getNickName() + ": " + ex.message
            )
        }
    }

}