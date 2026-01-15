package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.exception.chat.UnauthorizedAccessToChatException
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.repository.ChatRepository
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class AccountHasAccessToChatValidator(
    /* Repositories */
    private val accountRepository: AccountRepository,
    private val chatRepository: ChatRepository
): IValidatable<Pair<Long, Long>> {

    /**
     * Checks if the [Pair] (accountId, chatId) identifies an existing [com.yourRPG.chatPG.domain.Account], an existing
     *  [com.yourRPG.chatPG.domain.Chat] and if the account has access to the chat.
     *
     * @param [Pair] (accountId, chatId)
     * @return [Pair] given.
     * @throws AccountNotFoundException
     * @throws ChatNotFoundException
     * @throws UnauthorizedAccessToChatException
     */
    override fun validate(t: Pair<Long, Long>): Pair<Long, Long> {
        val (accountId, chatId) = t

        if (!accountRepository.existsById(accountId))
            throw AccountNotFoundException("Account $accountId not found")

        if (!chatRepository.existsById(chatId))
            throw ChatNotFoundException("Chat $chatId not found")

        if (!chatRepository.qExistsByAccountNameAndId(accountId, chatId))
            throw UnauthorizedAccessToChatException("Account $accountId cannot access chat $chatId")

        return t
    }

}