package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.service.poll.PollSubject
import org.springframework.data.jpa.repository.JpaRepository


interface PollRepository: JpaRepository<Poll, Poll.CompositePrimaryKey> {

    fun existsByChatIdAndSubject(chatId: Long, subject: PollSubject): Boolean

    fun findAllByChatId(chatId: Long): MutableList<Poll>

}