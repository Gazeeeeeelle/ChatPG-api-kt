package com.chatpg.controller

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.dto.poll.PollDto
import com.chatpg.service.poll.PollService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

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
        @PathVariable publicChatId: UUID,
        @RequestBody command: String
    ): ResponseEntity<PollDto> =
        ResponseEntity.ok(
            pollService.start(accountId, publicChatId, command)
        )

    /**
     * @see PollService.vote
     */
    @PostMapping(ApplicationEndpoints.Poll.VOTE)
    fun vote(
        @AuthenticationPrincipal accountId: Long,
        @PathVariable publicChatId: UUID,
        @RequestBody command: String
    ): ResponseEntity<PollDto> =
        ResponseEntity.ok(
            pollService.vote(accountId, publicChatId, command)
        )

    /**
     * @see PollService.all
     */
    @GetMapping(ApplicationEndpoints.Poll.ALL)
    fun getPolls(
        @PathVariable publicChatId: UUID
    ): ResponseEntity<List<PollDto>> =
         ResponseEntity.ok(
            pollService.all(publicChatId)
        )

}