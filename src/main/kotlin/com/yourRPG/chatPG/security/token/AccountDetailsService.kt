package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import javax.security.auth.login.AccountNotFoundException

@Service
class AccountDetailsService(
    /* Services */
    private val accountRepository: AccountRepository
): UserDetailsService {

    /**
     * Returns [UserDetails] of [com.yourRPG.chatPG.domain.Account] found by [username].
     *
     * @param username
     * @return [UserDetails] of the found [com.yourRPG.chatPG.domain.Account] by [username].
     * @see AccountService.getByName
     */
    override fun loadUserByUsername(username: String): UserDetails {
        return accountRepository.findByNameEquals(username)
            ?: throw AccountNotFoundException("User $username not found")
    }

}