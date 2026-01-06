package com.yourRPG.chatPG.service.account

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.exception.account.AccessToAccountUnauthorizedException
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.validator.PresenceValidator
import org.springframework.stereotype.Service

@Service
class AccountService(
    /* Repositories */
    private val repository: AccountRepository

): IConvertible<Account, AccountDto> {

    private val presenceValidator = PresenceValidator<Account>(AccountNotFoundException("Account not found"))

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
        return presenceValidator.validate(
            t = repository.findById(id).orElse(null)
        )
    }

    /**
     * Returns [AccountDto] by its respective [Account] name.
     *
     * @param name
     * @return [AccountDto] of the [Account] with name [name]
     * @throws AccountNotFoundException if no account was found under that name.
     */
    fun getDtoByName(name: String): AccountDto {
        val account = presenceValidator.validate(
            t = repository.findByNameEquals(name)
        )

        return account.toDto()
    }

    /**
     * If password matches return, else throw.
     *
     * @param accountId
     * @param password
     * @return [Boolean]
     * @throws AccountNotFoundException
     */
    fun checkPassword(accountId: Long, password: String) {
        if (!getById(accountId).passwordMatches(password)) {
            throw AccessToAccountUnauthorizedException("Wrong password")
        }
    }

}