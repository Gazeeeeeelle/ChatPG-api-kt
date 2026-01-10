package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.service.message.MessageService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/chats/{chatId}/messages")
class MessageController(
    private val service: MessageService
) {

    /**
     * @see MessageService.getNewMessagesInChat
     */
    @GetMapping("/new/{referenceId}")
    fun getNewMessages(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> =
        ResponseEntity.ok(
            service.getNewMessagesInChat(accountId, chatId, referenceId)
        )

    /**
     * @see MessageService.getOldMessagesInChat
     */
    @GetMapping("/old/{referenceId}")
    fun getOldMessages(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> =
        ResponseEntity.ok(
            service.getOldMessagesInChat(accountId, chatId, referenceId)
        )

    /**
     * @see MessageService.getDtoByChatIdAndId
     */
    @GetMapping("/{messageId}")
    fun getMessage(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable messageId: Long,
    ): ResponseEntity<MessageDto> =
        ResponseEntity.ok(
            service.getDtoByChatIdAndId(accountId, chatId, messageId)
        )

    /**
     * @see MessageService.sendMessage
     */
    @PostMapping("/send")
    fun sendMessage(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody content: String,
        ucb: UriComponentsBuilder
    ): ResponseEntity<MessageDto> =
        service.sendMessage(accountId, chatId, content).let { dto ->

            val uri = ucb.path("/chats/$chatId/messages/${dto.id}")
                .build().toUri()

            ResponseEntity.created(uri).body(dto)
        }

    /**
     * @see MessageService.generateResponse
     */
    @PostMapping("/generateResponse")
    fun generateResponse(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        ucb: UriComponentsBuilder
    ): ResponseEntity<MessageDto> =
        service.generateResponse(accountId, chatId).let { dto ->

            val uri = ucb.path("/chats/${chatId}/messages/${dto.id}")
                .build().toUri()

            ResponseEntity.created(uri).body(dto)
        }

    /**
     * @see MessageService.deleteMessage
     */
    @Transactional
    @DeleteMapping("/delete/{id}")
    fun deleteMessage(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable id: Long
    ): ResponseEntity<Int> =
        service.deleteMessage(accountId, chatId, id).let {
            ResponseEntity.noContent().build()
        }

    /**
     * @see MessageService.bulkDeleteMessages
     */
    @Transactional
    @DeleteMapping("/bulkDelete/{bound1}/{bound2}")
    fun bulkDeleteMessages(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable bound1: Long,
        @PathVariable bound2: Long
    ): ResponseEntity<Void> =
        service.bulkDeleteMessages(accountId, chatId, bound1, bound2).let {
            ResponseEntity.noContent().build()
        }

}