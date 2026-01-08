package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.poll.PollDto
import com.yourRPG.chatPG.service.poll.PollService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chats/{chatId}/polls")
class PollController(
    private val pollService: PollService
) {

    @PostMapping("/start")
    fun start(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody command: String
    ): ResponseEntity<PollDto> {
        return ResponseEntity.ok(
            pollService.start(accountId, chatId, command)
        )
    }

    @PostMapping("/vote")
    fun vote(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody command: String
    ): ResponseEntity<PollDto> {
        return ResponseEntity.ok(
            pollService.vote(accountId, chatId, command)
        )
    }

    @GetMapping("/all")
    fun vote(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long
    ): ResponseEntity<List<PollDto>> {
        return ResponseEntity.ok(
            pollService.all(accountId, chatId)
        )
    }

}