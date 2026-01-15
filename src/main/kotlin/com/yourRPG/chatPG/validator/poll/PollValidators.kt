package com.yourRPG.chatPG.validator.poll

import com.yourRPG.chatPG.domain.Chat
import com.yourRPG.chatPG.domain.Poll
import com.yourRPG.chatPG.validator.chat.AccountIsOwnerOfChatValidator
import com.yourRPG.chatPG.validator.poll.start.StartPollCredentialsValidator
import com.yourRPG.chatPG.validator.poll.vote.VotePollValidator
import org.springframework.stereotype.Component

@Component
class PollValidators(
    private val startPoll: StartPollCredentialsValidator,
    private val isOwner: AccountIsOwnerOfChatValidator,
    private val votePoll: VotePollValidator
) {

    /**
     * TODO
     */
    fun validateStart(accountId: Long, chat: Chat, poll: Poll) {

        isOwner.validate(t = accountId to chat)

        startPoll.validate(t = poll)

    }

    /**
     * TODO
     */
    fun validateVote(accountId: Long, poll: Poll) {

        votePoll.validate(t = accountId to poll)

    }

}