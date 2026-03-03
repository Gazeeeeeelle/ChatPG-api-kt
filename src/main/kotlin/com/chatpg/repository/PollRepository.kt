package com.chatpg.repository

import com.chatpg.domain.poll.Poll
import com.chatpg.service.poll.PollSubject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID


interface PollRepository: JpaRepository<Poll, Poll.CompositePrimaryKey> {

    fun existsByChatIdAndSubject(chatId: Long, subject: PollSubject): Boolean

    @Query("SELECT p FROM Poll p WHERE p.chat.publicId = :publicChatId")
    fun qFindAllByPublicChatId(publicChatId: UUID): List<Poll>

}