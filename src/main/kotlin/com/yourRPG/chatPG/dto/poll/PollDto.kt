package com.yourRPG.chatPG.dto.poll

import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.service.poll.PollSubject

data class PollDto(
    val chat: ChatDto,
    val subject: PollSubject,
    val quota: Int,
    val votes: Int
)
