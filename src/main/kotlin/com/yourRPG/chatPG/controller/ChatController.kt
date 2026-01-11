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

    /**
     * @see ChatService.getChatsDtoByAccountId
     * FIXME: scalability issue
     */
    @GetMapping("/all")
    fun getChats(
        @AuthenticationPrincipal accountId: Long
    ): ResponseEntity<List<ChatDto>> =
        ResponseEntity.ok(
            service.getChatsDtoByAccountId(accountId)
        )

    /**
     * @see ChatService.getDtoByAccountIdAndChatId
     */
    @GetMapping("/byName/{chatName}")
    fun getChat(
        @PathVariable chatName: String
    ): ResponseEntity<ChatDto> =
        ResponseEntity.ok(
            service.getDtoByAccountIdAndChatId(chatName)
        )

    /**
     * @see ChatService.chooseModelForChat
     */
    @PatchMapping("/{chatId}/chooseModel")
    fun chooseModel(
        @PathVariable chatId: Long,
        @RequestBody modelName: String
    ): ResponseEntity<Void> =
        service.chooseModelForChat(chatId, modelName).let {
            ResponseEntity.noContent().build()
        }

    /**
     * @see ChatService.getModelDto
     */
    @GetMapping("/{chatId}/chosenModel") //TODO rename 'chosenModel' to simply 'model'
    fun getChosenModel(
        @PathVariable chatId: Long
    ): ResponseEntity<AiModelDto> =
        ResponseEntity.ok(
            service.getModelDto(chatId)
        )

}