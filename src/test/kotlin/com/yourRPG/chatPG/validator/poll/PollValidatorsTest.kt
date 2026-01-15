package com.yourRPG.chatPG.validator.poll

import com.yourRPG.chatPG.domain.Chat
import com.yourRPG.chatPG.domain.Poll
import com.yourRPG.chatPG.validator.chat.AccountIsOwnerOfChatValidator
import com.yourRPG.chatPG.validator.poll.start.StartPollCredentialsValidator
import com.yourRPG.chatPG.validator.poll.vote.VotePollValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PollValidatorsTest {

    @InjectMocks
    lateinit var validator: PollValidators

    @field:Mock
    lateinit var startPoll: StartPollCredentialsValidator

    @field:Mock
    lateinit var isOwner: AccountIsOwnerOfChatValidator

    @field:Mock
    lateinit var votePoll: VotePollValidator

    val poll: Poll = mock(Poll::class.java)

    val chat: Chat = mock(Chat::class.java)

    @Test
    fun validateStart() {

        validator.validateStart(accountId = 0L, chat, poll)

        verify(isOwner, times(1))
            .validate(t = 0L to chat)

        verify(startPoll, times(1))
            .validate(t = poll)

    }

    @Test
    fun validateVote() {

        validator.validateVote(accountId = 0L, poll)

        verify(votePoll, times(1))
            .validate(t = 0L to poll)

    }

}