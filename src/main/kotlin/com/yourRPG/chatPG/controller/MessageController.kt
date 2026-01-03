package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.service.message.MessageService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/accounts/{accountId}/chats/{chatId}/messages")
class MessageController(
    private val messageService: MessageService
) {

    @GetMapping("/new/{referenceId}")
    fun getNewMessages(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> {
        return ResponseEntity.ok(
            messageService.getNewMessagesInChat(accountId, chatId, referenceId)
        )
    }

    @GetMapping("/old/{referenceId}")
    fun getOldMessages(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable referenceId: Long,
    ): ResponseEntity<List<MessageDto>> {
        return ResponseEntity.ok(
            messageService.getOldMessagesInChat(accountId, chatId, referenceId)
        )
    }

    @PostMapping("/send")
    fun sendMessage(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody content: String
    ): ResponseEntity<MessageDto> {
        return ResponseEntity.status(201).body(
            messageService.sendMessage(accountId, chatId, message = content)
        )
    }

    @PostMapping("/generateResponse")
    fun generateResponse(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long
    ): ResponseEntity<MessageDto> {
        return ResponseEntity.status(201).body(
            messageService.generateResponse(accountId, chatId)
        )
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    fun deleteMessage(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable id: Long
    ): ResponseEntity<Int> {
        messageService.deleteMessage(accountId, chatId, id)

        return ResponseEntity.status(204).build()
    }

    @Transactional
    @DeleteMapping("/bulkDelete/{bound1}/{bound2}")
    fun bulkDeleteMessages(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable bound1: Long,
        @PathVariable bound2: Long
    ): ResponseEntity<Void> {
        messageService.bulkDeleteMessages(accountId, chatId, bound1, bound2)

        return ResponseEntity.status(204).build()
    }

}