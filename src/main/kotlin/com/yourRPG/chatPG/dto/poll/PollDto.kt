package com.yourRPG.chatPG.dto.poll

import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.service.poll.PollSubject
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
