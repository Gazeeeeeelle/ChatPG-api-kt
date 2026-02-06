package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.service.message.MessageService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping(ApplicationEndpoints.Message.BASE)
class MessageController(
    private val service: MessageService
) {

    /**
     * @see MessageService.getNewMessagesInChat
     */
    @GetMapping(ApplicationEndpoints.Message.NEW)
    fun getNewMessages(
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> =
        ResponseEntity.ok(
            service.getNewMessagesInChat(chatId, referenceId)
        )

    /**
     * @see MessageService.getOldMessagesInChat
     */
    @GetMapping(ApplicationEndpoints.Message.OLD)
    fun getOldMessages(
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> =
        ResponseEntity.ok(
            service.getOldMessagesInChat(chatId, referenceId)
        )

    /**
     * @see MessageService.getDtoByChatIdAndId
     */
    @GetMapping(ApplicationEndpoints.Message.GET_MESSAGE)
    fun getMessage(
        @PathVariable chatId: Long,
        @PathVariable messageId: Long,
    ): ResponseEntity<MessageDto> =
        ResponseEntity.ok(
            service.getDtoByChatIdAndId(chatId, messageId)
        )

    /**
     * @see MessageService.sendMessage
     */
    @PostMapping
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
    @PostMapping(ApplicationEndpoints.Message.GENERATE_RESPONSE)
    fun generateResponse(
        @PathVariable chatId: Long,
        ucb: UriComponentsBuilder
    ): ResponseEntity<MessageDto> =
        service.generateResponse(chatId).let { dto ->

            val path = ApplicationEndpoints.Message.GET_MESSAGE
                .replace("{chatId}", chatId.toString())
                .replace("{messageId}", dto.id.toString())

            val uri = ucb.path(path).build().toUri()
            ResponseEntity.created(uri).body(dto)
        }

    /**
     * @see MessageService.deleteMessage
     */
    @Transactional
    @DeleteMapping(ApplicationEndpoints.Message.DELETE)
    fun deleteMessage(
        @PathVariable chatId: Long,
        @PathVariable messageId: Long
    ): ResponseEntity<Int> =
        service.deleteMessage(chatId, messageId).let {
            ResponseEntity.noContent().build()
        }

    /**
     * @see MessageService.bulkDeleteMessages
     */
    @Transactional
    @DeleteMapping(ApplicationEndpoints.Message.BULK_DELETE)
    fun bulkDeleteMessages(
        @PathVariable chatId: Long,
        @PathVariable bound1: Long,
        @PathVariable bound2: Long
    ): ResponseEntity<Void> =
        service.bulkDeleteMessages(chatId, bound1, bound2).let {
            ResponseEntity.noContent().build()
        }

}