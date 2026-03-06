package com.chatpg.service.ai

import com.chatpg.exception.ai.NullAiResponse
import com.chatpg.service.ai.providers.AiModel
import com.chatpg.service.ai.providers.AiProvider
import org.springframework.stereotype.Service

@Service
class AiService(
    private val providers: List<IAiProviderService>
) {

    /**
     * Directs a request to an AI model based on [AiModel] and such request has content [prompt].
     * Returns a nullable string with the content of the AI's response.
     *
     * @param AiModel
     * @param String
     * @return nullable [String] containing the AI's response
     * @throws NullAiResponse if the content of the response's message was null.
     */
    fun askAi(model: AiModel, prompt: String): String {
        require(model.provider != AiProvider.NONE) { "Model cannot be \"none\" to request ai message." }

        return providers.find { it.getProvider() == model.provider }
            ?.askAi(model, prompt)
            ?: throw NullAiResponse("Response from ${model.nickname} was null")
    }

    /**
     * Returns [Boolean] based on whether the an [AiModel] was found with the given nickname [nickname]
     *
     * @param nickname
     * @return [Boolean] for if an [AiModel] was found with nickname [nickname]
     */
    fun isModelAvailable(nickname: String): Boolean =
        AiModel.findByNickName(nickname) != null

    /**
     * Returns a [List] of [String]s containing all the available model's nicknames
     *
     * @return [List] of [String]s with available model's nicknames
     */
    fun getModels(): List<String> =
        AiModel.entries.map { it.nickname }

}