package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.service.ai.AiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/models")
class AiModelController(
    private val aiService: AiService
) {

    @GetMapping("/isModelAvailable/{modelName}")
    fun isModelAvailable(@PathVariable modelName: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(
            aiService.isModelAvailable(modelName)
        )
    }

    @GetMapping("/all")
    fun all(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(
            aiService.getModels()
        )
    }

}