package com.yourRPG.chatPG.service.account

import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.validator.account.AccountCreationCredentialsValidator
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class AccountService(
    /* Repositories */
    private val repository: AccountRepository,

    private val accountCreationCredentialsValidator: AccountCreationCredentialsValidator
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
     *  if the id did not identify an account
     */
    fun getById(id: Long): Account =
        repository.findByIdOrNull(id)
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

    /**
     * Returns the [Account] found by its name [accountName].
     *
     * @param accountName name of the account being searched for
     * @return [Account] found by name [accountName].
     * @throws AccountNotFoundException
     *  if the name did not identify an account
     */
    fun getByName(accountName: String): Account =
        repository.findByNameEquals(accountName)
            ?: throw AccountNotFoundException("Account not found with name $accountName")

    /**
     * Updates the [account] in the DB with the given [uuid] and sets [Account.uuidBirth] to be
     * [Instant.now].
     *
     * @param account who is getting their uuid set.
     * @param uuid uuid to set.
     */
    fun updateUuid(account: Account, uuid: UUID?) {
        account.apply {
            this.uuid = uuid
            this.uuidBirth = uuid?.run { Instant.now() }
        }
        repository.save(account)
    }

    /**
     * Returns the [Account] found with [UUID] [uuid].
     *
     * @param uuid
     * @return Account found with matching [UUID]s.
     * @throws AccountNotFoundException if no account was found with [UUID] [uuid]
     */
    fun getByUuid(uuid: UUID): Account {
        return repository.findByUuidEquals(uuid)
            ?: throw AccountNotFoundException("No account found with uuid given")
    }

    /**
     * Updates the [account] in the DB with the given password
     */
    fun updatePassword(account: Account, encryptedPassword: String) {
        account.accountPassword = encryptedPassword
        repository.save(account)
    }

    fun getByEmail(email: String): Account {
        return repository.findByEmail(email)
            ?: throw AccountNotFoundException("Account not found with email $email")
    }

    /**
     * Inserts new [Account].
     *
     * @param username
     * @param email
     * @param encryptedPassword
     * @return [AccountDto] of the inserted [Account]
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    fun saveAccountWith(username: String, email: String, encryptedPassword: String): Account {

        accountCreationCredentialsValidator.validate(username to email)

        val account = Account(username, email, encryptedPassword)

        repository.save(account)

        return account
    }

    fun deleteById(id: Long?) {
        repository.deleteById(id ?: throw AccountNotFoundException("Account not found with id $id"))
    }

    fun updateStatus(account: Account, status: AccountStatus) {
        account.status = status
        repository.save(account)
    }

}