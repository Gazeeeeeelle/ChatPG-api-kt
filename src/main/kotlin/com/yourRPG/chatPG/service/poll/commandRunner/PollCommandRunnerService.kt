package com.yourRPG.chatPG.service.poll.commandRunner

import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.service.message.MessageService
import com.yourRPG.chatPG.service.poll.PollSubject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class PollCommandRunnerService(
    //Services
    private val messageService: MessageService
) {

    /**
     * TODO
     */
    fun run(poll: Poll) {
        when (poll.subject) {
            PollSubject.REQUEST_AI_MESSAGE -> messageService.generateResponse(
                accountId = poll.chat?.ownerId ?: -1,
                chatId    = poll.chat?.id      ?: -1
            )

            else -> throw IllegalStateException("Unexpected value")
        }
    }

}