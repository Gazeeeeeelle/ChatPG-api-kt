package com.chatpg.validator.poll

import com.chatpg.domain.chat.Chat
import com.chatpg.domain.poll.Poll
import com.chatpg.validator.chat.AccountIsOwnerOfChatValidator
import com.chatpg.validator.poll.start.StartPollCredentialsValidator
import com.chatpg.validator.poll.vote.VotePollValidator
import org.springframework.stereotype.Component

@Component
class PollValidators(
    private val startPoll: StartPollCredentialsValidator,
    private val isOwner: AccountIsOwnerOfChatValidator,
    private val votePoll: VotePollValidator
) {

    /**
     * @see AccountIsOwnerOfChatValidator.validate
     * @see StartPollCredentialsValidator.validate
     */
    fun validateStart(accountId: Long, chat: Chat, poll: Poll) {

        isOwner.validate(t = accountId to chat)

        startPoll.validate(t = poll)

    }

    /**
     * @see VotePollValidator.validate
     */
    fun validateVote(accountId: Long, poll: Poll) {

        votePoll.validate(t = accountId to poll)

    }

}