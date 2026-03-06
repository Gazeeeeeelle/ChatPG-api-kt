package com.chatpg.validator.poll.start

import com.chatpg.exception.chat.ChatNotFoundException
import com.chatpg.exception.poll.PollAlreadyExistsException
import com.chatpg.domain.chat.Chat
import com.chatpg.domain.poll.Poll
import com.chatpg.repository.PollRepository
import com.chatpg.service.poll.PollSubject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class StartPollCredentialsValidatorTest {

    @InjectMocks
    lateinit var validator: StartPollCredentialsValidator

    @Mock lateinit var pollRepository: PollRepository

    val poll: Poll = Mockito.mock(Poll::class.java)
    val chat: Chat = Mockito.mock(Chat::class.java)

    @Test
    fun valid() {
        //ARRANGE
        given(poll.chat)
            .willReturn(chat)

        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(poll)
        }
    }

    @Test
    fun invalid_chatNotFound() {
        //ARRANGE
        given(poll.subject)
            .willReturn(PollSubject.NONE)

        given(poll.chat)
            .willReturn(chat)

        given(chat.id)
            .willReturn(null)

        //ACT + ASSERT
        assertThrows<ChatNotFoundException> {
            validator.validate(poll)
        }
    }

    @Test
    fun invalid_pollAlreadyExistsException() {
        //ARRANGE
        given(poll.chat)
            .willReturn(chat)

        given(poll.subject)
            .willReturn(PollSubject.NONE)

        given(pollRepository.existsByChatIdAndSubject(0L, PollSubject.NONE))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<PollAlreadyExistsException> {
            validator.validate(poll)
        }
    }

}