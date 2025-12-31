package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.model.Message
import org.springframework.stereotype.Service

@Service
class ChatpgService {

    private val aiPrefix: String = "[AI: ]"

    fun treatMemoryForPrompt(msgList: MutableList<Message>): String {
        val stringBuilder = StringBuilder()

        msgList.forEach(action = {
            m: Message? -> stringBuilder
                .append(if (m?.isBot() == true) aiPrefix else getAccountPrefix(m?.getAccount()))
                .append(m?.getContent()).append("\n")
        })


        return stringBuilder.toString()
    }

    private fun getAccountPrefix(account: Account?): String {
        return "[USER(" + (account?.getName() ?: "!!! NO_NAME !!!") + "): ]"
    }

}