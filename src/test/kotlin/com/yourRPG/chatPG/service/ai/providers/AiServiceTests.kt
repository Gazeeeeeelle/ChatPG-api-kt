package com.yourRPG.chatPG.service.ai.providers

import com.yourRPG.chatPG.service.ai.AiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AiServiceTests {

    @Autowired
    private lateinit var service: AiService

    companion object {
        const val TEST_PROMPT: String =
            "This message was sent from a scripted test. Respond with \"I'm active and accessible\"."

        const val NO_ANSWER_PROMPT: String =
        "This message was sent from a scripted test. Respond with \"I'm active and accessible\"."
    }

}