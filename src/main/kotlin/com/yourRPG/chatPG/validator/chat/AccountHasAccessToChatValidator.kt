package com.yourRPG.chatPG.validator.chat

import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.exception.chat.ForbiddenAccessToChatException
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.repository.ChatRepository
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AccountHasAccessToChatValidator(
    private val accountRepository: AccountRepository,
    private val chatRepository: ChatRepository
): IValidatable<Pair<Long, UUID>> {

    /**
     * Checks if the [Pair] (accountId, chatId) identifies an existing [com.yourRPG.chatPG.domain.account.Account], an existing
     *  [com.yourRPG.chatPG.domain.chat.Chat] and if the account has access to the chat.
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