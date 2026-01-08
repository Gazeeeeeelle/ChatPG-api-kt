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
    private val messageService: MessageService
) {

    @GetMapping("/new/{referenceId}")
    fun getNewMessages(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> {
        return ResponseEntity.ok(
            messageService.getNewMessagesInChat(accountId, chatId, referenceId)
        )
    }

    @GetMapping("/old/{referenceId}")
    fun getOldMessages(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> {
        return ResponseEntity.ok(
            messageService.getOldMessagesInChat(accountId, chatId, referenceId)
        )
    }

    @GetMapping("/{id}")
    fun getMessage(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable id: Long,
    ): ResponseEntity<MessageDto> {
        return ResponseEntity.ok(
            messageService.getMessage(accountId, chatId, id)
        )
    }

    @PostMapping("/send")
    fun sendMessage(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody content: String,
        ucb: UriComponentsBuilder
    ): ResponseEntity<MessageDto> {
        val dto: MessageDto = messageService.sendMessage(accountId, chatId, message = content)

        val uri = ucb
            .path("/api/accounts/${accountId}/chats/${chatId}/messages/${dto.id}")
            .build()
            .toUri()

        return ResponseEntity.created(uri).body(dto)
    }

    @PostMapping("/generateResponse")
    fun generateResponse(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        ucb: UriComponentsBuilder
    ): ResponseEntity<MessageDto> {
        val dto: MessageDto = messageService.generateResponse(accountId, chatId)

        val uri = ucb
            .path("/api/accounts/${accountId}/chats/{chatId}/messages/${dto.id}")
            .build()
            .toUri()

        return ResponseEntity.created(uri).body(dto)
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    fun deleteMessage(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable id: Long
    ): ResponseEntity<Int> {
        messageService.deleteMessage(accountId, chatId, id)

        return ResponseEntity.noContent().build()
    }

    @Transactional
    @DeleteMapping("/bulkDelete/{bound1}/{bound2}")
    fun bulkDeleteMessages(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable bound1: Long,
        @PathVariable bound2: Long
    ): ResponseEntity<Void> {
        messageService.bulkDeleteMessages(accountId, chatId, bound1, bound2)

        return ResponseEntity.noContent().build()
    }

}