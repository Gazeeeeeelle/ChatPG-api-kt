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
    fun getById(id: Long): Account =
        repository.findById(id).orElse(null)
            ?: throw AccountNotFoundException("Account not found with id $id")

    /**
     * Delegates fetching of Account to [getById] and then converts it to DTO.
     *
     * @param accountId
     * @return [AccountDto] of the identified [Account]
     *
     * @see getById
     */
    fun getDtoById(accountId: Long): AccountDto =
        getById(accountId).toDto()

    fun existsByName(accountName: String): Boolean =
        repository.existsByNameEquals(accountName)

    fun getByName(accountName: String): Account =
        repository.findByNameEquals(accountName)
            ?: throw AccountNotFoundException("Account not found with name $accountName")

}