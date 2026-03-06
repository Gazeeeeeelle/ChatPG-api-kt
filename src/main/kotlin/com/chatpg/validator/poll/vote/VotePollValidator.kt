package com.chatpg.validator.poll.vote

import com.chatpg.exception.poll.AlreadyVotedInPollException
import com.chatpg.domain.poll.Poll
import com.chatpg.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class VotePollValidator: IValidatable<Pair<Long, Poll>> {

    override fun validate(t: Pair<Long, Poll>) {

        if (t.second.votes.contains(t.first))
            throw AlreadyVotedInPollException("You already voted in this poll")

    }

}