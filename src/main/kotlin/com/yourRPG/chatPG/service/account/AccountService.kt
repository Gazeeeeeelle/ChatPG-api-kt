package com.yourRPG.chatPG.service.account

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.validator.PresenceValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountService: IConvertible<Account, AccountDto> {

    //Repositories
    @Autowired
    private lateinit var repository: AccountRepository

    //Validators
    private val presenceValidator = PresenceValidator<Account>(AccountNotFoundException("Account not found"))

    //Conversion
    override fun Account.dto(): AccountDto {
        return AccountDto(this)
    }

    /**
     * Returns [Account] by its id
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
     * Returns [AccountDto] by its respective [Account] name
     *
     * @param name
     * @return [AccountDto]
     * @throws AccountNotFoundException
     * if no account was found under that name
     */
    fun getDtoByName(name: String): AccountDto {
        val account = presenceValidator.validate(
            t = repository.findByNameEquals(name)
        )

        return account.dto()
    }

    /**
     * Returns [Boolean] based on whether the account found via [accountId]'s password matches [password]
     *
     * @param accountId
     * @param password
     * @return [Boolean]
     * @throws AccountNotFoundException
     */
    fun checkPassword(accountId: Long, password: String): Boolean {
        return getById(accountId).passwordMatches(password)
    }

}