package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.poll.PollDto
import com.yourRPG.chatPG.service.poll.PollService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/accounts/{accountId}/chats/{chatId}/polls")
class PollController {

    @Autowired
    private lateinit var pollService: PollService

    @PostMapping("/start")
    fun start(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody command: String
    ): ResponseEntity<PollDto> {
        return ResponseEntity.ok(
            pollService.start(accountId, chatId, command)
        )
    }

    @PostMapping("/vote")
    fun vote(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody command: String
    ): ResponseEntity<PollDto> {
        return ResponseEntity.ok(
            pollService.vote(accountId, chatId, command)
        )
    }

    @GetMapping("/all")
    fun vote(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long
    ): ResponseEntity<MutableList<PollDto>> {
        return ResponseEntity.ok(
            pollService.all(accountId, chatId)
        )
    }

}