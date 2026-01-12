package com.yourRPG.chatPG.service.ai

import com.yourRPG.chatPG.dto.ai.model.AiModelDto
import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiProvider
import org.springframework.stereotype.Service

@Service
class AiService(
    /* Inject providers */
    private val providers: List<IResponsive>
): IConvertible<AiModel, AiModelDto> {

    /**
     * Conversion.
     * @see IConvertible
     */
    override fun dtoOf(c: AiModel): AiModelDto = AiModelDto(c)

    /**
     * Directs a request to an AI model based on [AiModel] and such request has content [prompt].
     * Returns a nullable string with the content of the AI's response.
     *
     * @param AiModel
     * @param String
     * @return nullable [String] containing the AI's response
     * @throws com.yourRPG.chatPG.exception.BadRequestException if the model given had provider [AiProvider.NONE]
     * @throws com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
     * if the AI's response could not fit the expected object, likely because the response was an error object, and therefore it is judged as an Internal Server Error from the provider's end.
     */
    fun askAi(model: AiModel, prompt: String): String? {
        require(model.provider == AiProvider.NONE) { "Model cannot be \"none\" to request ai message." }

        return providers.find {it.getProvider() == model.provider}
            ?.askAi(model, prompt)
            ?: throw UnavailableAiException("Provider ${model.provider} not implemented.")
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