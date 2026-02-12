package com.yourRPG.chatPG.service.ai.providers

import com.yourRPG.chatPG.infra.external.ai.chutes.ChutesLlmApiService
import com.yourRPG.chatPG.service.ai.IResponsive
import org.springframework.stereotype.Service

@Service
class ChutesAiService(
    private val chutesLlmApiService: ChutesLlmApiService
): IResponsive {

    override fun getProvider(): AiProvider = AiProvider.CHUTES


    /**
     * @see IResponsive.askAi
     */
    override fun askAi(model: AiModel, prompt: String): String = chutesLlmApiService.askAi(model, prompt)

}