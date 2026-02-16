package com.yourRPG.chatPG.controller

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApplicationEndpoints.Account.BASE)
class AccountController(
    private val accountService: AccountService
) {

    /**
     * @see AccountService.getDtoById
     */
    @GetMapping(ApplicationEndpoints.Account.CURRENT)
    fun getAccount(
        @AuthenticationPrincipal accountId: Long,
    ): ResponseEntity<AccountDto> =
        ResponseEntity.ok(
            accountService.getDtoById(accountId)
        )

}
