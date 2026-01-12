package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.model.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRepository: JpaRepository<Chat, Long> {

    /**
     * Returns all chats the account identified by [accountId] has access to.
     * FIXME: scalability problem
     *
     * @param accountId account identifier.
     * @return [List] of [Chat]s the account has access to.
     */
    @Query("SELECT c FROM Chat c JOIN c.accounts a ON a.id = :accountId")
    fun qFindByAccountId(accountId: Long): List<Chat>

    /**
     * Returns an existing [Chat] by its name.
     *
     * @param chatName
     * @return nullable [Chat]
     */
    @Query("SELECT c FROM Chat c WHERE c.name = :chatName")
    fun qFindByName(chatName: String): Chat?

    /**
     * Returns [Boolean] based on whether a [Chat] is found in the chats that the [com.yourRPG.chatPG.model.Account] has
     *  access to and has id of [id].
     *
     * @param accountId account identifier.
     * @param id chat identifier.
     * @return [Boolean] based on whether such chat exists for the account or not.
     */
    @Query("SELECT CASE WHEN (COUNT(c) > 0) THEN true ELSE false END FROM Chat c JOIN c.accounts a ON a.id = :accountId AND c.id = :id")
    fun qExistsByAccountNameAndId(accountId: Long, id: Long): Boolean

    /**
     * Returns the amount of accounts with access to the chat identified by [chatId].
     *
     * @param chatId chat identifier.
     * @return amount of accounts with access to the chat.
     */
    @Query("SELECT COUNT(a) FROM Chat c JOIN c.accounts a WHERE c.id = :chatId")
    fun countAccounts(chatId: Long): Int

}