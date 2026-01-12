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
     * Runs a command based on the [poll]'s subject.
     *
     * @param poll
     * @throws IllegalStateException if the subject does not have a treatment
     */
    fun run(poll: Poll) {
        when (poll.subject) {
            PollSubject.REQUEST_AI_MESSAGE -> messageService.createAIMessage(poll.chat)

            else -> throw IllegalStateException("Unexpected value")
        }
    }

}