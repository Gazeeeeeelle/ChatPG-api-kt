package com.yourRPG.chatPG.service.ai.providers.chutes

import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.service.ai.providers.AiServiceTests
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@ExtendWith(MockitoExtension::class)
class ChutesAiServiceTests {

    @Autowired
    private lateinit var service: ChutesAiService

    @Test
    fun gptOss20b() {
        Assertions.assertDoesNotThrow({
            println(service.askAi(
                model  = AiModel.GPT_OSS_20B,
                prompt = AiServiceTests.NO_ANSWER_PROMPT
            ))
        })
    }

    @Test
    fun tongyiDeepResearch30BA3B() {
        Assertions.assertDoesNotThrow({
            println(service.askAi(
                model  = AiModel.TONGYI_DEEP_RESEARCH_30B_A3B,
                prompt = AiServiceTests.NO_ANSWER_PROMPT
            ))
        })
    }

    @Test
    fun gemma34BIt() {
        Assertions.assertDoesNotThrow({
            println(service.askAi(
                model  = AiModel.GEMMA_3_4B_IT,
                prompt = AiServiceTests.NO_ANSWER_PROMPT
            ))
        })
    }


}