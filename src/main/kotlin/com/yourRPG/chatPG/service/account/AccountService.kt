package com.yourRPG.chatPG.service.account

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.service.IConvertible
import org.springframework.stereotype.Service

@Service
class AccountService(
    /* Repositories */
    private val repository: AccountRepository

): IConvertible<Account, AccountDto> {

    private val notFoundException = AccountNotFoundException("Account not found")

    /**
     * Conversion.
     * @see IConvertible
     */
    override fun dtoOf(c: Account): AccountDto = AccountDto(c)

    /**
     * Returns an [Account] by its id.
     *
     * @param id
     * @return [Account]
     * @throws AccountNotFoundException
     * if the id did not identify an account
     */
    fun getById(id: Long): Account {
        return repository.findById(id).orElse(null)
            ?: throw notFoundException
    }

    fun getDtoById(accountId: Long): AccountDto {
        return getById(accountId).toDto()
    }

}