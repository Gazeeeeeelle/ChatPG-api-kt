package com.yourRPG.chatPG.service.ai.providers.google

import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiServiceTests
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class GoogleAiServiceTests{

    @Autowired
    private lateinit var service: AiService

    @Test
    fun gemini25Flash() {
        Assertions.assertDoesNotThrow({
            println(service.askAi(
                model  = AiModel.GEMINI_2_5_FLASH,
                prompt = AiServiceTests.TEST_PROMPT
            ))
        })
    }

}