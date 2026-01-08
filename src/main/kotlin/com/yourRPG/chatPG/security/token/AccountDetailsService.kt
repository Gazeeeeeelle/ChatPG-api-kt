package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.repository.AccountRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AccountDetailsService(
    /* Repositories */
    private val accountRepository: AccountRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return accountRepository.findByNameEquals(username)
            ?: throw AccountNotFoundException("Account not found: $username")
    }

}