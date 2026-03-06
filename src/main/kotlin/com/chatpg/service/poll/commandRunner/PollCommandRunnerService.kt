package com.chatpg.service.poll.commandRunner

import com.chatpg.domain.poll.Poll
import com.chatpg.service.message.MessageService
import com.chatpg.service.poll.PollSubject
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

            PollSubject.NONE -> throw IllegalStateException("Poll subject has Subject NONE")
        }
    }

}