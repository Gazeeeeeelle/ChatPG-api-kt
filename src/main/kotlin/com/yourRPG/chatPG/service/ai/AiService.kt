package com.yourRPG.chatPG.service.ai

import com.yourRPG.chatPG.dto.ai.model.AiModelDto
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiProvider
import com.yourRPG.chatPG.service.ai.providers.chutes.ChutesAiService
import com.yourRPG.chatPG.service.ai.providers.google.GoogleAiService
import org.apache.coyote.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AiService: IConvertible<AiModel, AiModelDto> {

    //Service
    @Autowired
    private lateinit var googleAIService: GoogleAiService

    @Autowired
    private lateinit var chutesAIService: ChutesAiService

    //Conversion
    override fun (AiModel).dto(): AiModelDto {
        return AiModelDto(nickname = this.getNickName())
    }

    /**
     * Directs a request to an AI model based on [aiModel] and such request has content [prompt].
     * Returns a nullable string with the content of the AI's response.
     *
     * @param AiModel
     * @param String
     * @return nullable [String]
     * @throws com.yourRPG.chatPG.exception.BadRequestException if the model given had provider [AiProvider.NONE]
     * @throws com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
     * if the AI's response could not fit the expected object, likely because the response was an error object, and therefore it is judged as a Internal Server Error from the provider's end.
     */
    fun askAI(aiModel: AiModel, prompt: String): String? {
        return when (aiModel.getProvider()) {
            AiProvider.NONE -> throw BadRequestException("Chat's model cannot be \"none\" to request ai message.")
            AiProvider.GOOGLE -> googleAIService.askAi(aiModel, prompt)
            AiProvider.CHUTES -> chutesAIService.askAi(aiModel, prompt)
        }
    }

    /**
     * Returns [Boolean] based on whether the an [AiModel] was found with the given nickname [nickname]
     *
     * @param nickname
     * @return [Boolean]
     */
    fun isModelAvailable(nickname: String): Boolean {
        return AiModel.findByNickName(nickname) != null
    }

    /**
     * Returns a [MutableList] of [String]s containing all the available model's nicknames
     *
     * @return [MutableList] of [String]s
     */
    fun getModels(): MutableList<String> {
        return AiModel.entries
            .map { it.getNickName() }
            .toMutableList()
    }

}