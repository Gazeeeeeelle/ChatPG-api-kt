package com.yourRPG.chatPG.validator.poll.vote

import com.yourRPG.chatPG.exception.poll.AlreadyVotedInPollException
import com.yourRPG.chatPG.model.Poll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class VotePollValidatorTest {

    val votePollValidator: VotePollValidator = VotePollValidator()

    val poll: Poll = mock(Poll::class.java)

    val t: Pair<Long, Poll> = Pair(0L, poll)

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