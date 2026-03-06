package com.chatpg.dto.poll

import com.chatpg.dto.chat.ChatDto
import com.chatpg.service.poll.PollSubject
import org.jetbrains.annotations.NotNull

data class PollDto(

    @field:NotNull
    val chat: ChatDto?,

    @field:NotNull
    val subject: PollSubject?,

    @field:NotNull
    val quota: Int?,

    @field:NotNull
    val votes: Int?

)
