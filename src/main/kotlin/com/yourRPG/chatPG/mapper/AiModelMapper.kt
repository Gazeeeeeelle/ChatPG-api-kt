package com.yourRPG.chatPG.mapper

import com.yourRPG.chatPG.dto.aimodel.AiModelDto
import com.yourRPG.chatPG.service.ai.providers.AiModel
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface AiModelMapper {

    fun toDto(aiModel: AiModel): AiModelDto

}