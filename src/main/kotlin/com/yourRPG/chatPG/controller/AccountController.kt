package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService
) {

    /**
     * @see AccountService.getDtoById
     */
    @GetMapping("/current")
    fun getAccount(
        @AuthenticationPrincipal accountId: Long,
    ): ResponseEntity<AccountDto> {
        return ResponseEntity.ok(accountService.getDtoById(accountId))
    }

    /**
     * @see AccountService.existsByName
     */
    @PostMapping("/exists")
    fun accountExists(
        @RequestBody accountName: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(accountService.existsByName(accountName))
    }

}