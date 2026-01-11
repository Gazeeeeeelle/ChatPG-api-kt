package com.yourRPG.chatPG.service.poll.commandRunner

import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.service.message.MessageService
import com.yourRPG.chatPG.service.poll.PollSubject
import org.springframework.stereotype.Service


@Service
class PollCommandRunnerService(
    /* Services */
    private val messageService: MessageService
) {

    /**
     * TODO
     */
    fun run(poll: Poll) {
        when (poll.subject) {
            PollSubject.REQUEST_AI_MESSAGE -> messageService.generateResponse(
                chatId = poll.chat?.id
                    ?: throw ChatNotFoundException("Null chat id")
            )

            else -> throw IllegalStateException("Unexpected value")
        }
    }

}