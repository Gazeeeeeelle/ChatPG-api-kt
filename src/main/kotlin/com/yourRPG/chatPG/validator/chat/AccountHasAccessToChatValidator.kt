package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.chat.UnauthorizedAccessToChatException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
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
     * Checks if the [Pair] (accountId, chatId) identifies an existing [com.yourRPG.chatPG.model.Account], an existing
     *  [com.yourRPG.chatPG.model.Chat] and if the account has access to the chat.
     *
     * @param [Pair] (accountId, chatId)
     * @return [Pair] given.
     * @throws AccountNotFoundException
     * @throws ChatNotFoundException
     * @throws UnauthorizedAccessToChatException
     */
    override fun validate(t: Pair<Long, Long>): Pair<Long, Long> {
        require(accountRepository.existsById(t.first)) {
            throw AccountNotFoundException("Account with id ${t.first} not found")
        }

        require(chatRepository.existsById(t.second)) {
            throw ChatNotFoundException("Chat with id ${t.first} not found")
        }

        require(chatRepository.qExistsByAccountIdAndId(t.first, t.second)) {
            throw UnauthorizedAccessToChatException("Account with id ${t.first} cannot access chat with id ${t.first}")
        }

        return t
    }

}