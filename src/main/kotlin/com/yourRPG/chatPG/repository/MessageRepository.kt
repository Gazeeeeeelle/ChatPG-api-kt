package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Message
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query


interface MessageRepository: JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :id")
    fun qFindByChatId(id: Long?): MutableList<Message>

    @Query("SELECT m FROM Message m JOIN Chat c ON m.chat.name = c.name JOIN Account a ON a.name = :accountName")
    fun qFindByChatNameAndAccountName(accountName: String?, chatName: String?): MutableList<Message>

    @Query("SELECT m FROM Message m WHERE m.chat = :c")
    fun qFindAllMessagesFromChat(c: Chat?): MutableList<Message>

    @Query("SELECT m FROM Message m WHERE m.chat.id = :id")
    fun qFindAllByChatId(id: Long?): MutableList<Message>

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.chat.id = :chatId AND m.id = :id")
    fun qDeleteByChatIdAndId(chatId: Long, id: Long): Int

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.chat.id = :chatId AND m.id BETWEEN :idStart AND :idFinish")
    fun qBulkDeleteByChatIdFromIdToId(chatId: Long, idStart: Long, idFinish: Long): Int

}