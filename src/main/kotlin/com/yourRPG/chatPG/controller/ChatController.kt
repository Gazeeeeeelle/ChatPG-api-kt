package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.chat.AiModelDto
import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.service.chat.ChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/accounts/{accountId}/chats")
class ChatController {

    @Autowired
    private lateinit var chatService: ChatService

    @GetMapping("/all")
    fun getChats(
        @PathVariable accountId: Long
    ): ResponseEntity<MutableList<ChatDto>> {
        val chats: MutableList<ChatDto> = chatService.getChats(accountId)

        return ResponseEntity.ok(chats)
    }

    @GetMapping("/byName/{chatName}")
    fun getChat(
        @PathVariable accountId: Long,
        @PathVariable chatName: String
    ): ResponseEntity<ChatDto> {
        val chat = chatService.getDtoByAccountIdAndChatName(
            accountId,
            chatName
        )

        return ResponseEntity.ok(chat)
    }

    @PatchMapping("/{chatId}/chooseModel")
    fun chooseModel(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody modelName: String
    ): ResponseEntity<Void> {
        chatService.chooseModelForChat(accountId, chatId, modelName)
        return ResponseEntity.status(204).build()
    }

    @GetMapping("/{chatId}/chosenModel")
    fun getChosenModel(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long
    ): ResponseEntity<AiModelDto> {
        val model: AiModelDto = chatService.getChosenModel(accountId, chatId)
        return ResponseEntity.ok(model)
    }


}