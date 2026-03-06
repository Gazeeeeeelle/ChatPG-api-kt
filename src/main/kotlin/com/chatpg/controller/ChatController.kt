package com.chatpg.controller

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.dto.aimodel.AiModelDto
import com.chatpg.dto.chat.ChatDto
import com.chatpg.service.chat.ChatService
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
        @AuthenticationPrincipal accountId: Long,
    ): ResponseEntity<List<ChatDto>> =
        ResponseEntity.ok(
            service.getChatsDtoByAccountId(accountId),
        )

    /**
     * @see ChatService.getDtoByChatName
     */
    @GetMapping(ApplicationEndpoints.Chat.BY_NAME)
    fun getChatByName(
        @PathVariable chatName: String,
    ): ResponseEntity<ChatDto> =
        ResponseEntity.ok(
            service.getDtoByChatName(chatName)
        )

    /**
     * @see ChatService.updateChatModel
     */
    @PatchMapping(ApplicationEndpoints.Chat.MODEL)
    fun chooseModel(
        @PathVariable publicChatId: UUID,
        @RequestBody modelName: String
    ): ResponseEntity<Void> =
        service.updateChatModel(publicChatId, modelName).let {
            ResponseEntity.noContent().build()
        }

    /**
     * @see ChatService.getModelDto
     */
    @GetMapping(ApplicationEndpoints.Chat.MODEL)
    fun getChosenModel(
        @PathVariable publicChatId: UUID
    ): ResponseEntity<AiModelDto> =
        ResponseEntity.ok(
            service.getModelDto(publicChatId)
        )

}
