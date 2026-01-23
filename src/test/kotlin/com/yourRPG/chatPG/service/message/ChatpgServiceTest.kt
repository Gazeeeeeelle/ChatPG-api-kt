package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.domain.Chat
import com.yourRPG.chatPG.domain.Message
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ChatpgServiceTest {

    private val chatpgService = ChatpgService()

    @Mock
    private lateinit var account: Account

    @Mock
    private lateinit var chat: Chat

    @Test
    fun treatMemoryForPrompt() {
        //ARRANGE
        val messages = listOf(
            Message(account       , chat, content = "first message" , bot = false),
            Message(account = null, chat, content = "second message", bot = true ),
            Message(account       , chat, content = "third message" , bot = false),
        )

        val username = "account_username"

        given(account.name)
            .willReturn(username)

        //ACT
        val actual = chatpgService.treatMemoryForPrompt(messages)

        val expected = """
            [USER($username):]first message
            [AI:]second message
            [USER($username):]third message
        """.trimIndent()

        //ASSERT
        assertEquals(expected, actual)
    }

}