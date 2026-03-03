package com.chatpg.validator.chat

import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.chat.ChatNotFoundException
import com.chatpg.exception.chat.ForbiddenAccessToChatException
import com.chatpg.repository.AccountRepository
import com.chatpg.repository.ChatRepository
import com.chatpg.validator.IValidatable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AccountHasAccessToChatValidator(
    private val accountRepository: AccountRepository,
    private val chatRepository: ChatRepository
): IValidatable<Pair<Long, UUID>> {

    /**
     * Checks if the [Pair] (accountId, chatId) identifies an existing [com.chatpg.domain.account.Account], an existing
     *  [com.chatpg.domain.chat.Chat] and if the account has access to the chat.
     *
     * @param [Pair] (accountId, chatId)
     * @return [Pair] given.
     * @throws AccountNotFoundException
     * @throws ChatNotFoundException
     * @throws ForbiddenAccessToChatException
     */
    override fun validate(t: Pair<Long, UUID>) {
        val (accountId, publicChatId) = t

        if (!accountRepository.existsById(accountId))
            throw AccountNotFoundException("Account $accountId not found")

        if (!chatRepository.qExistsByPublicId(publicChatId))
            throw ChatNotFoundException("Chat not found with public id given")

        if (!chatRepository.qExistsByAccountNameAndId(accountId, publicChatId))
            throw ForbiddenAccessToChatException("Access denied")

    }

}