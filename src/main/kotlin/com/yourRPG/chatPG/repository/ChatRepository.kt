package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.model.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRepository: JpaRepository<Chat, Long> {

    /**
     * Returns all chats the account identified by [accountId] has access to.
     * FIXME: scalability problem
     *
     * @param accountId account identifier
     * @return [List] of [Chat]s the account has access to.
     */
    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.id = :accountId")
    fun qFindByAccountId(accountId: Long): List<Chat>

    /**
     * Returns a [Chat] with id of [id] that the account identified by [accountId] has access to. If not found
     *  returns null.
     *
     * @param accountId account identifier
     * @param id chat identifier
     * @return nullable [Chat]
     */
    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.id = :accountId AND c.id = :id")
    fun qFindByAccountIdAndId(accountId: Long, id: Long): Chat?

    /**
     * Returns a [Chat] with name [chatName] that the account identified by [accountId] has access to. If not found
     *  returns null.
     *
     * @param accountId account identifier
     * @param chatName
     * @return nullable [Chat]
     */
    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.id = :accountId AND c.name = :chatName")
    fun qFindByAccountNameAndName(accountId: Long, chatName: String): Chat?

    /**
     * Returns [Boolean] based on whether a [Chat] is found in the chats that the [com.yourRPG.chatPG.model.Account] has
     *  access to and has id of [id].
     *
     * @param accountId account identifier
     * @param id chat identifier
     * @return [Boolean] based on whether such chat exists for the account or not.
     */
    @Query("SELECT CASE WHEN (COUNT(c) > 0) THEN true ELSE false END FROM Chat c JOIN c.accounts a ON a.id = :accountId AND c.id = :id")
    fun qExistsByAccountNameAndId(accountId: Long, id: Long): Boolean

}