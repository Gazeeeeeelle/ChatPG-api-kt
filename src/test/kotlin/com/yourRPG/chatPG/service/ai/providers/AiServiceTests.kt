package com.yourRPG.chatPG.service.ai.providers

import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.service.ai.AiService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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

    @Test
    fun gemini25Flash() {
        Assertions.assertDoesNotThrow({
            println(service.askAi(
                model  = AiModel.GEMINI_2_5_FLASH,
                prompt = TEST_PROMPT
            ))
        })
    }

    @Test
    fun gptOss20b() {
        Assertions.assertThrows(UnavailableAiException::class.java, {
            println(service.askAi(
                model  = AiModel.GPT_OSS_20B,
                prompt = TEST_PROMPT
            ))
        })
    }

}