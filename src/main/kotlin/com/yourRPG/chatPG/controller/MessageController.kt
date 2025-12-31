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
class MessageController {

    @Autowired
    private lateinit var messageService: MessageService;

    @GetMapping("/all")
    fun getMessages(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long
    ): ResponseEntity<MutableList<MessageDto>> {
        return ResponseEntity.ok(
            messageService.getMessagesInChat(accountId, chatId)
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
    @DeleteMapping("/bulkDelete/{idStart}/{idFinish}")
    fun bulkDeleteMessages(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @PathVariable idStart: Long,
        @PathVariable idFinish: Long
    ): ResponseEntity<Void> {
        messageService.bulkDeleteMessages(accountId, chatId, idStart, idFinish)

        return ResponseEntity.status(204).build()
    }

}