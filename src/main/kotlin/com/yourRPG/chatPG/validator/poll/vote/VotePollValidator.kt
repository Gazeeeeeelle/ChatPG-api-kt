package com.yourRPG.chatPG.validator.poll.vote

import com.yourRPG.chatPG.exception.poll.AlreadyVotedInPollException
import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class VotePollValidator: IValidatable<Pair<Long, Poll>> {

    override fun validate(t: Pair<Long, Poll>): Pair<Long, Poll> {

        if (t.second.votes.contains(t.first))
            throw AlreadyVotedInPollException("You already voted in this poll")

        return t
    }

}