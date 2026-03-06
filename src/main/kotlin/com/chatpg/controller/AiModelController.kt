package com.chatpg.controller

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.service.ai.AiService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApplicationEndpoints.AiModel.BASE)
class AiModelController(
    private val aiService: AiService
) {

    /**
     * @see isModelAvailable
     */
    @GetMapping(ApplicationEndpoints.AiModel.IS_MODEL_AVAILABLE)
    fun isModelAvailable(
        @PathVariable modelName: String
    ): ResponseEntity<Boolean> =
        ResponseEntity.ok(
            aiService.isModelAvailable(modelName)
        )

    /**
     * @see AiService.getModels
     */
    @GetMapping(ApplicationEndpoints.AiModel.ALL)
    fun all(): ResponseEntity<List<String>> =
        ResponseEntity.ok(
            aiService.getModels()
        )

}