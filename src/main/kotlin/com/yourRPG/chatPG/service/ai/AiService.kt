package com.yourRPG.chatPG.service.ai

import com.yourRPG.chatPG.dto.chat.AiModelDto
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiProvider
import com.yourRPG.chatPG.service.ai.providers.chutes.ChutesAiService
import com.yourRPG.chatPG.service.ai.providers.google.GoogleAiService
import org.apache.coyote.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class AiService: IConvertible<AiModel, AiModelDto> {

    @Autowired
    private lateinit var googleAIService: GoogleAiService

    @Autowired
    private lateinit var chutesAIService: ChutesAiService

    fun askAI(aiModel: AiModel, prompt: String): String? {
        return when (aiModel.getProvider()) {
            AiProvider.NONE -> throw BadRequestException("Chat's model cannot be \"none\" to request ai message.")
            AiProvider.GOOGLE -> googleAIService.askAi(aiModel, prompt)
            AiProvider.CHUTES -> chutesAIService.askAi(aiModel, prompt)
        }
    }

    fun isModelAvailable(modelName: String): Boolean {
        return AiModel.findByNickName(modelName) != null
    }

    fun getModels(): MutableList<String> {
        return AiModel.entries
            .map { it.getNickName() }
            .toMutableList()
    }

    override fun convert(c: AiModel): AiModelDto {
        return AiModelDto(nickname = c.getNickName())
    }

}