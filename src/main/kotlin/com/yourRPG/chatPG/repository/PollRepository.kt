package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.service.poll.PollSubject
import org.springframework.data.jpa.repository.JpaRepository


interface PollRepository: JpaRepository<Poll, Poll.CompositePrimaryKey> {

    fun existsByChatIdAndSubject(chat: Chat, subject: PollSubject): Boolean

    fun findAllByChatId(chat: Chat): List<Poll>

}