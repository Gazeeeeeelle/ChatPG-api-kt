package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.domain.Message
import org.springframework.stereotype.Service
import javax.security.auth.login.AccountNotFoundException

@Service
class ChatpgService {

    //Fixed prefix for AI message
    private companion object {
        const val AI_PREFIX: String = "[AI:]"
    }

    /**
     * Receives a [List] of [Message]s. For all messages in the list the following is done:
     * * If the message was sent from the bot, then use prefix [AI_PREFIX], else [getAccountPrefix].
     * * Append next to the prefix that indicates the account that sent the message, or if it is a bot, the message's
     *  content.
     * Then, the format for each message becomes:
     * * If user: `[USER(accountName):]messageContent`
     * * If bot: `[AI:]messageContent`
     *
     * @param messages
     * @return [String] containing the formatted pattern for all messages in the list provided.
     * @throws AccountNotFoundException
     * @see getAccountPrefix
     */
    fun treatMemoryForPrompt(messages: List<Message>): String =
        buildString {
            messages.forEach { m: Message ->
                append(when {
                    m.bot -> AI_PREFIX
                    else -> getAccountPrefix(m.account)
                })
                append(m.content)
                append("\n")
            }
        }

    /**
     * Given an [Account], it returns a [String] with the following pattern:
     * * `[USER(accountName):]`
     *
     * @param account
     * @return [String] formatted to the pattern
     * @throws AccountNotFoundException
     */
    private fun getAccountPrefix(account: Account?): String =
        "[USER(${account?.name ?: throw AccountNotFoundException()}):]"

}