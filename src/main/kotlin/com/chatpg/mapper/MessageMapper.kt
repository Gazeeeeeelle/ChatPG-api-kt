package com.chatpg.mapper

import com.chatpg.domain.message.Message
import com.chatpg.dto.message.MessageDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface MessageMapper {

    fun toDto(message: Message): MessageDto

}