package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/accounts")
class AccountController {

    @Autowired
    private lateinit var accountService: AccountService;

    @GetMapping("/byName/{accountName}")
    fun getAccount(
        @PathVariable accountName: String
    ): ResponseEntity<AccountDto> {
        val account: AccountDto = accountService.getByName(accountName)
        return ResponseEntity.ok(account)
    }

    @PostMapping("/{accountId}/passwordMatches")
    fun passwordMatches(
        @PathVariable accountId: Long,
        @RequestBody password: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(accountService.checkPassword(accountId, password))
    }

}