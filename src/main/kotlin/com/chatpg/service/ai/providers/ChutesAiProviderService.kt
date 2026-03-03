package com.chatpg.service.ai.providers

import com.chatpg.infra.external.ai.chutes.ChutesLlmApiService
import com.chatpg.service.ai.IAiProviderService
import org.springframework.stereotype.Service

@Service
class ChutesAiProviderService(
    private val chutesLlmApiService: ChutesLlmApiService
): IAiProviderService {

    override fun getProvider(): AiProvider = AiProvider.CHUTES


    /**
     * @see IAiProviderService.askAi
     */
    override fun askAi(model: AiModel, prompt: String): String = chutesLlmApiService.askAi(model, prompt)

}