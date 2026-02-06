package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.dto.poll.PollDto
import com.yourRPG.chatPG.service.poll.PollService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApplicationEndpoints.Poll.BASE)
class PollController(
    private val pollService: PollService
) {

    /**
     * @see PollService.start
     */
    @PostMapping(ApplicationEndpoints.Poll.START)
    fun start(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody command: String
    ): ResponseEntity<PollDto> =
        ResponseEntity.ok(
            pollService.start(accountId, chatId, command)
        )

    /**
     * @see PollService.vote
     */
    @PostMapping(ApplicationEndpoints.Poll.VOTE)
    fun vote(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable chatId: Long,
        @RequestBody command: String
    ): ResponseEntity<PollDto> =
        ResponseEntity.ok(
            pollService.vote(accountId, chatId, command)
        )

    /**
     * @see PollService.all
     */
    @GetMapping(ApplicationEndpoints.Poll.ALL)
    fun getPolls(
        @PathVariable chatId: Long
    ): ResponseEntity<List<PollDto>> =
         ResponseEntity.ok(
            pollService.all(chatId)
        )

}