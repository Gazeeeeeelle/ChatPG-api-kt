package com.yourRPG.chatPG.service.poll.commandRunner

import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.service.message.MessageService
import com.yourRPG.chatPG.service.poll.PollSubject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class PollCommandRunnerService {

    //Services
    @Autowired
    private lateinit var messageService: MessageService

    /**
     * TODO
     */
    fun run(poll: Poll) {
        when (poll.getSubject()) {
            PollSubject.REQUEST_AI_MESSAGE -> messageService.generateResponse(
                accountId = poll.getChat()?.getOwnerId() ?: -1,
                chatId    = poll.getChat()?.getId()      ?: -1
            )

            else -> throw IllegalStateException("Unexpected value")
        }
    }

}