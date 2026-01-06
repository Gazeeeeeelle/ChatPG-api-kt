package com.yourRPG.chatPG.validator.poll.start

import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.exception.poll.PollAlreadyExistsException
import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.repository.PollRepository
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class StartPollCredentialsValidator(
    /* Repositories */
    private val pollRepository: PollRepository

): IValidatable<Poll> {

    override fun validate(t: Poll): Poll {

        val alreadyExists: Boolean = pollRepository.existsByChatIdAndSubject(
            chatId = t.chat?.id
                ?: throw ChatNotFoundException("Chat id cannot be null"),
            subject = t.subject
        )

        if (alreadyExists)
            throw PollAlreadyExistsException("Cannot start a new poll because one of the same matter is already active")

        return t
    }

}