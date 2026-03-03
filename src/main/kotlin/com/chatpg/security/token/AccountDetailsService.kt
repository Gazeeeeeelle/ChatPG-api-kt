package com.chatpg.security.token

import com.chatpg.repository.AccountRepository
import com.chatpg.service.account.AccountService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import javax.security.auth.login.AccountNotFoundException

@Service
class AccountDetailsService(
    private val accountRepository: AccountRepository
): UserDetailsService {

    /**
     * Returns [UserDetails] of [com.chatpg.domain.account.Account] found by [username].
     *
     * @param username
     * @return [UserDetails] of the found [com.chatpg.domain.account.Account] by [username].
     * @see AccountService.getByName
     */
    override fun loadUserByUsername(username: String): UserDetails {
        return accountRepository.findByNameEquals(username)
            ?: throw AccountNotFoundException("User $username not found")
    }

}