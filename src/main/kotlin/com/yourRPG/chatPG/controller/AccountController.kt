package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @GetMapping("/byName/{accountName}")
    fun getAccount(
        @PathVariable accountName: String
    ): ResponseEntity<AccountDto> {
        return ResponseEntity.ok(
            accountService.getDtoByName(accountName)
        )
    }

    @PostMapping("/{accountId}/passwordMatches")
    fun passwordMatches(
        @PathVariable accountId: Long,
        @Valid @RequestBody password: String
    ): ResponseEntity<Boolean> {
        accountService.checkPassword(accountId, password)
        return ResponseEntity.ok(true)
    }

}