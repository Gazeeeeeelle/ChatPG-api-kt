package com.chatpg.mapper

import com.chatpg.dto.aimodel.AiModelDto
import com.chatpg.service.ai.providers.AiModel
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface AiModelMapper {

    fun toDto(aiModel: AiModel): AiModelDto

}