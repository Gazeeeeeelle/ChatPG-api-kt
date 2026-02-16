package com.yourRPG.chatPG.mapper

import com.yourRPG.chatPG.domain.message.Message
import com.yourRPG.chatPG.dto.message.MessageDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface MessageMapper {

    fun toDto(message: Message): MessageDto

}