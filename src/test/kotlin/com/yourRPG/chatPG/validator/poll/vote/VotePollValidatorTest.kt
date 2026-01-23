package com.yourRPG.chatPG.validator.poll.vote

import com.yourRPG.chatPG.exception.poll.AlreadyVotedInPollException
import com.yourRPG.chatPG.domain.Poll
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class VotePollValidatorTest {

    private val votePollValidator: VotePollValidator = VotePollValidator()

    private val poll: Poll = mock(Poll::class.java)

    private val t: Pair<Long, Poll> = Pair(0L, poll)

    @Test
    fun valid() {
        //ACT + ASSERT
        given(t.second.votes)
            .willReturn(hashSetOf())

        assertDoesNotThrow {
            votePollValidator.validate(t)
        }
    }

    @Test
    fun invalid() {
        //ASSERT
        given(t.second.votes)
            .willReturn(hashSetOf(t.first))

        //ACT + ASSERT
        assertThrows<AlreadyVotedInPollException> {
            votePollValidator.validate(t)
        }
    }

}