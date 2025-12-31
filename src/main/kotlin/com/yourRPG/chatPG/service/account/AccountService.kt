package com.yourRPG.chatPG.service.account

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.service.ICanNotBeFound
import com.yourRPG.chatPG.service.IConvertible
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountService: IConvertible<Account, AccountDto>, ICanNotBeFound {

    @Autowired
    private lateinit var accountRepo: AccountRepository;


    override fun convert(c: Account): AccountDto {
        return AccountDto(c)
    }

    override fun getNotFoundException(): RuntimeException {
        return AccountNotFoundException("Account not found")
    }


    fun getPureById(id: Long): Account {
        return accountRepo.findById(id)
            .orElseThrow { AccountNotFoundException("Account not found") }
    }

    fun getByName(name: String): AccountDto {
        accountRepo.findByNameEquals(name)
            ?.let {return AccountDto(it)}

        throw AccountNotFoundException("Account not found")
    }


    /* @TODO: REFACTOR ME INTO A VALIDATOR */
    fun checkPassword(accountId: Long, password: String): Boolean {
        return getPureById(accountId).passwordMatches(password)
    } /* !TODO: REFACTOR ME INTO A VALIDATOR */




}