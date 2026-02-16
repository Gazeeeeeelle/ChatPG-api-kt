package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.dto.aimodel.AiModelDto
import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.service.chat.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping(ApplicationEndpoints.Chat.BASE)
class ChatController(
    private val service: ChatService
) {

    /**
     * @see ChatService.getChatsDtoByAccountId
     * FIXME: minor scalability issue
     */
    @GetMapping(ApplicationEndpoints.Chat.ALL)
    fun getChats(
        @AuthenticationPrincipal accountId: Long
    ): ResponseEntity<List<ChatDto>> =
        ResponseEntity.ok(
            service.getChatsDtoByAccountId(accountId)
        )

    /**
     * @see ChatService.getDtoByChatName
     */
    @GetMapping(ApplicationEndpoints.Chat.BY_NAME)
    fun getChatByName(
        @PathVariable chatName: String
    ): ResponseEntity<ChatDto> =
        ResponseEntity.ok(
            service.getDtoByChatName(chatName)
        )

    /**
     * @see ChatService.updateChatModel
     */
    @PatchMapping(ApplicationEndpoints.Chat.CHOOSE_MODEL)
    fun chooseModel(
        @PathVariable publicId: UUID,
        @RequestBody modelName: String
    ): ResponseEntity<Void> =
        service.updateChatModel(publicId, modelName).let {
            ResponseEntity.noContent().build()
        }

    /**
     * @see ChatService.getModelDto
     */
    @GetMapping(ApplicationEndpoints.Chat.MODEL)
    fun getChosenModel(
        @PathVariable publicId: UUID
    ): ResponseEntity<AiModelDto> =
        ResponseEntity.ok(
            service.getModelDto(publicId)
        )

}