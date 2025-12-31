package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.model.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRepository: JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.name = :accountName AND c.name = :chatName")
    fun qFindByAccountNameAndChatName(accountName: String, chatName: String): Chat?

    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.id = :id")
    fun qFindByAccountId(id: Long): MutableList<Chat>

    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.id = :accountId AND c.id = :id")
    fun qFindByAccountIdAndId(accountId: Long, id: Long): Chat?

    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.id = :accountId AND c.name = :chatName")
    fun qFindByAccountIdAndChatName(accountId: Long, chatName: String): Chat?

}