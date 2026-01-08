package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.ai.model.AiModelDto
import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.service.chat.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chats")
class ChatController(
    private val service: ChatService
) {

    @GetMapping("/all")
    fun getChats(
        @AuthenticationPrincipal accountId: Long
    ): ResponseEntity<List<ChatDto>> {
        return ResponseEntity.ok(
            service.getChatsDtoByAccountId(accountId)
        )
    }

    @GetMapping("/byName/{chatName}")
    fun getChat(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatName: String
    ): ResponseEntity<ChatDto> {
        return ResponseEntity.ok(
            service.getDtoByAccountIdAndChatId(accountId, chatName)
        )
    }

    @PatchMapping("/{chatId}/chooseModel")
    fun chooseModel(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody modelName: String
    ): ResponseEntity<Void> {
        service.chooseModelForChat(accountId, chatId, modelName)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{chatId}/chosenModel")
    fun getChosenModel(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long
    ): ResponseEntity<AiModelDto> {
        return ResponseEntity.ok(
            service.getModelDto(accountId, chatId)
        )
    }

}