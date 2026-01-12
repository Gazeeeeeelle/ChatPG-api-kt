package com.yourRPG.chatPG.validator.poll.start

import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.exception.poll.PollAlreadyExistsException
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.repository.PollRepository
import com.yourRPG.chatPG.service.poll.PollSubject
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

    @field:Mock
    lateinit var pollRepository: PollRepository

    val t: Poll = Mockito.mock(Poll::class.java)

    val chat: Chat = Mockito.mock(Chat::class.java)

    @Test
    fun valid() {
        //ARRANGE
        given(t.chat)
            .willReturn(chat)

        //ACT + ASSERT
        assertDoesNotThrow {
            validator.validate(t)
        }
    }

    @Test
    fun invalid_chatNotFound() {
        //ARRANGE
        given(t.subject)
            .willReturn(PollSubject.NONE)

        given(t.chat)
            .willReturn(chat)

        given(chat.id)
            .willReturn(null)

        //ACT + ASSERT
        assertThrows<ChatNotFoundException> {
            validator.validate(t)
        }
    }

    @Test
    fun invalid_pollAlreadyExistsException() {
        //ARRANGE
        given(t.chat)
            .willReturn(chat)

        given(t.subject)
            .willReturn(PollSubject.NONE)

        given(chat.id)
            .willReturn(0L)

        given(pollRepository.existsByChatIdAndSubject(0L, PollSubject.NONE))
            .willReturn(true)

        //ACT + ASSERT
        assertThrows<PollAlreadyExistsException> {
            validator.validate(t)
        }
    }

}