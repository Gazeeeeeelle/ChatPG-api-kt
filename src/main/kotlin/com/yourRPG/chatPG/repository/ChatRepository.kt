package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.domain.chat.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ChatRepository: JpaRepository<Chat, Long> {

    /**
     * Returns all chats the account identified by [accountId] has access to.
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
     * Returns [Boolean] based on whether a [Chat] is found in the chats that the [com.yourRPG.chatPG.domain.account.Account] has
     *  access to and has id of [].
     *
     * @param accountId account identifier.
     * @param publicId chat identifier.
     * @return [Boolean] based on whether such chat exists for the account or not.
     */
    @Query("""
        SELECT 
            CASE 
                WHEN COUNT(c) > 0 THEN true 
                ELSE false 
            END 
        FROM Chat c 
        JOIN c.accounts a 
            ON a.id = :accountId 
        WHERE c.publicId = :publicId
    """)
    fun qExistsByAccountNameAndId(accountId: Long, publicId: UUID): Boolean

    /**
     * Returns the amount of accounts with access to the chat identified by [publicId].
     *
     * @param publicId chat identifier.
     * @return amount of accounts with access to the chat.
     */
    @Query("SELECT COUNT(a) FROM Chat c JOIN c.accounts a WHERE c.publicId = :publicId")
    fun countAccounts(publicId: UUID): Int

    @Query("SELECT c FROM Chat c WHERE c.publicId = :publicId")
    fun qFindByPublicId(publicId: UUID): Chat?

    @Query("""
        SELECT 
            CASE 
                WHEN COUNT(c) > 0 THEN true 
                ELSE false 
            END 
        FROM Chat c 
        WHERE c.publicId = :publicId
    """)
    fun qExistsByPublicId(publicId: UUID): Boolean

}