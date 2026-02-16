package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.domain.poll.Poll
import com.yourRPG.chatPG.service.poll.PollSubject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID


interface PollRepository: JpaRepository<Poll, Poll.CompositePrimaryKey> {

    fun existsByChatIdAndSubject(chatId: Long, subject: PollSubject): Boolean

    @Query("SELECT p FROM Poll p WHERE p.chat.publicId = :publicChatId")
    fun qFindAllByPublicChatId(publicChatId: UUID): List<Poll>

}