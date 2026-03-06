package com.chatpg.validator.poll.start

import com.chatpg.exception.chat.ChatNotFoundException
import com.chatpg.exception.poll.PollAlreadyExistsException
import com.chatpg.domain.poll.Poll
import com.chatpg.repository.PollRepository
import com.chatpg.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class StartPollCredentialsValidator(
    /* Repositories */
    private val pollRepository: PollRepository

): IValidatable<Poll> {

    override fun validate(t: Poll) {

        val alreadyExists: Boolean = pollRepository.existsByChatIdAndSubject(
            chatId = t.chat.id
                ?: throw ChatNotFoundException("No chat id found"),
            subject = t.subject
        )

        if (alreadyExists)
            throw PollAlreadyExistsException("Cannot start a new poll because one of the same matter is already active")

    }

}