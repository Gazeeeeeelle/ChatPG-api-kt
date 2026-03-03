package com.chatpg.mapper

import com.chatpg.domain.chat.Chat
import com.chatpg.dto.chat.ChatDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ChatMapper {

    fun toDto(chat: Chat): ChatDto

}