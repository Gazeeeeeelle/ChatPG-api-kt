package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.domain.chat.Chat
import com.yourRPG.chatPG.domain.message.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface MessageRepository: JpaRepository<Message, Long> {

    /**
     * Fetches all messages from the [chat].
     * FIXME: minor scalability issue, needs pagination
     *
     * @param chat from where the messages will be fetched
     * @return [List] of [Message]s fetched
     */
    @Query("SELECT m FROM Message m WHERE m.chat = :chat GROUP BY m.id ORDER BY m.id ASC")
    fun qFindAllMessagesFromChat(chat: Chat): List<Message>

    /**
     * Fetches 20 messages from the [Chat] identified with [publicChatId]. The messages are fetched by selecting the ids
     *  less than the [reference] id, and therefore, 20 older messages than the reference.
     *
     * @param publicChatId chat identifier
     * @param reference reference id for fetching
     * @return [List] of [Message]s fetched
     */
    @Query("SELECT m FROM Message m WHERE m.chat.publicId = :publicChatId AND m.id < :reference GROUP BY m.id ORDER BY m.id DESC LIMIT 20")
    fun qFindOldByChatIdAndReference(publicChatId: UUID, reference: Long): List<Message>

    /**
     * Fetches 20 messages from the [Chat] identified with [publicChatId]. The messages are fetched by selecting the ids
     *  greater than the [reference] id, and therefore, 20 newer messages than the reference.
     *
     * @param publicChatId chat identifier
     * @param reference reference id for fetching
     * @return [List] of [Message]s fetched
     */
    @Query("SELECT m FROM Message m WHERE m.chat.publicId = :publicChatId AND m.id > :reference GROUP BY m.id ORDER BY m.id DESC LIMIT 20")
    fun qFindNewByChatIdAndReference(publicChatId: UUID, reference: Long): List<Message>

    /**
     * Fetches one message from the [Chat] identified with [publicChatId], such that the message's id = [messageId].
     *
     * @param publicChatId chat identifier
     * @param messageId message identifier
     * @return [Message] found or else null
     */
    @Query("SELECT m FROM Message m WHERE m.chat.publicId = :publicChatId AND m.id = :messageId")
    fun findByPublicChatIdAndId(publicChatId: UUID, messageId: Long): Message?

    /**
     * Deletes a single message, with id [messageId], in the chat identified by [publicChatId].
     *
     * @param publicChatId chat identifier.
     * @param messageId message identifier.
     * @return Integer amount of messages deleted. One if successful, zero if no message were found for deletion.
     */
    @Modifying
    @Query("DELETE FROM Message m WHERE m.chat.publicId = :publicChatId AND m.id = :messageId")
    fun qDeleteByChatIdAndId(publicChatId: UUID, messageId: Long): Int

    /**
     * Deletes a multiple messages within a chat. The chat is identified by [publicChatId]. The range of messages deleted are
     *  the ones with id between and including [idStart] and [idFinish].
     *
     * @param publicChatId chat identifier.
     * @param idStart lower bound of the ids range.
     * @param idFinish upper bound of the ids range.
     * @return Integer amount of messages deleted. Bigger than zero if successful, zero if no message found for deletion.
     */
    @Modifying
    @Query("DELETE FROM Message m WHERE m.chat.publicId = :publicChatId AND m.id BETWEEN :idStart AND :idFinish")
    fun qBulkDeleteByChatIdFromIdToId(publicChatId: UUID, idStart: Long, idFinish: Long): Int

}