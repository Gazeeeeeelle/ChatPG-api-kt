package com.yourRPG.chatPG.service.ai

import com.yourRPG.chatPG.service.ai.providers.AiModel

interface IResponsive {

    /**
     * TODO
     */
    fun askAi(model: AiModel, prompt: String): String?

}