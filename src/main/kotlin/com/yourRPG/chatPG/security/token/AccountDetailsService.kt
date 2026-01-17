package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AccountDetailsService(
    /* Services */
    private val accountService: AccountService
): UserDetailsService {

    /**
     * Returns [UserDetails] of [com.yourRPG.chatPG.domain.Account] found by [username].
     *
     * @param username
     * @return [UserDetails] of the found [com.yourRPG.chatPG.domain.Account] by [username].
     * @see AccountService.getByName
     */
    override fun loadUserByUsername(username: String): UserDetails {
        return accountService.getByName(username)
    }

}