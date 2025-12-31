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
        val dto = pollService.start(accountId, chatId, command)
        return ResponseEntity.ok(dto)
    }

    @PostMapping("/vote")
    fun vote(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody command: String
    ): ResponseEntity<PollDto> {
        val dto = pollService.vote(accountId, chatId, command)

        return ResponseEntity.ok(dto)
    }

    @GetMapping("/all")
    fun vote(
        @PathVariable accountId: Long,
        @PathVariable chatId: Long
    ): ResponseEntity<MutableList<PollDto>> {
        val dto: MutableList<PollDto> = pollService.all(accountId, chatId)

        return ResponseEntity.ok(dto)
    }

}