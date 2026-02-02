package com.yourRPG.chatPG.mapper

import com.yourRPG.chatPG.domain.chat.Chat
import com.yourRPG.chatPG.dto.chat.ChatDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ChatMapper {

    fun toDto(chat: Chat): ChatDto

}