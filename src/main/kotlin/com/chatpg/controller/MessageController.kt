package com.chatpg.controller

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.dto.message.MessageDto
import com.chatpg.service.message.MessageService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

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
        @PathVariable publicChatId: UUID,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> =
        ResponseEntity.ok(
            service.getNewMessagesInChat(publicChatId, referenceId)
        )

    /**
     * @see MessageService.getOldMessagesInChat
     */
    @GetMapping(ApplicationEndpoints.Message.OLD)
    fun getOldMessages(
        @PathVariable publicChatId: UUID,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> =
        ResponseEntity.ok(
            service.getOldMessagesInChat(publicChatId, referenceId)
        )

    /**
     * @see MessageService.getDtoByChatIdAndId
     */
    @GetMapping(ApplicationEndpoints.Message.GET_MESSAGE)
    fun getMessage(
        @PathVariable publicChatId: UUID,
        @PathVariable messageId: Long,
    ): ResponseEntity<MessageDto> =
        ResponseEntity.ok(
            service.getDtoByChatIdAndId(publicChatId, messageId)
        )

    /**
     * @see MessageService.sendMessage
     */
    @PostMapping
    fun sendMessage(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable publicChatId: UUID,
        @RequestBody content: String,
        ucb: UriComponentsBuilder
    ): ResponseEntity<MessageDto> =
        service.sendMessage(accountId, publicChatId, content).let { dto ->

            val uri = ucb.path("/chats/$publicChatId/messages/${dto.id}")
                .build().toUri()

            ResponseEntity.created(uri).body(dto)
        }

    /**
     * @see MessageService.generateResponse
     */
    @PostMapping(ApplicationEndpoints.Message.GENERATE_RESPONSE)
    fun generateResponse(
        @PathVariable publicChatId: UUID,
        ucb: UriComponentsBuilder
    ): ResponseEntity<MessageDto> {
        val response = service.generateResponse(publicChatId)

        val path = ApplicationEndpoints.Message.GET_MESSAGE
            .replace("{publicChatId}", publicChatId.toString())
            .replace("{messageId}"   , response.id.toString() )

        val uri = ucb.path(path).build().toUri()
        return ResponseEntity.created(uri).body(response)
    }

    /**
     * @see MessageService.deleteMessage
     */
    @Transactional
    @DeleteMapping(ApplicationEndpoints.Message.DELETE)
    fun deleteMessage(
        @PathVariable publicChatId: UUID,
        @PathVariable messageId: Long
    ): ResponseEntity<Int> =
        service.deleteMessage(publicChatId, messageId).let {
            ResponseEntity.noContent().build()
        }

    /**
     * @see MessageService.bulkDeleteMessages
     */
    @Transactional
    @DeleteMapping(ApplicationEndpoints.Message.BULK_DELETE)
    fun bulkDeleteMessages(
        @PathVariable publicChatId: UUID,
        @PathVariable bound1: Long,
        @PathVariable bound2: Long
    ): ResponseEntity<Void> =
        service.bulkDeleteMessages(publicChatId, bound1, bound2).let {
            ResponseEntity.noContent().build()
        }

}