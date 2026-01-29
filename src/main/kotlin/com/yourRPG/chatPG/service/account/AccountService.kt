package com.yourRPG.chatPG.service.account

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.validator.account.AccountCreationCredentialsValidator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

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
     * Returns the [Account] found by its name [username].
     *
     * @param username name of the account being searched for
     * @return [Account] found by name [username].
     * @throws AccountNotFoundException
     *  if the name did not identify an account
     */
    fun getByName(username: String): Account =
        repository.findByNameEquals(username)
            ?: throw AccountNotFoundException("Account not found with name $username")

    /**
     * Returns the [Account] found with [UUID] [uuid].
     *
     * @param uuid
     * @return Account found with matching [UUID]s.
     * @throws AccountNotFoundException if no account was found with [UUID] [uuid]
     */
    fun getByUuid(uuid: UUID): Account {
        return repository.qFindByUuidEquals(uuid)
            ?: throw AccountNotFoundException("No account found with uuid given")
    }

    /**
     * Returns the Account found with [email] given.
     *
     * @param email used to search for the account.
     * @return [Account] found.
     * @throws AccountNotFoundException
     */
    fun getByEmail(email: String): Account {
        return repository.qFindByEmail(email)
            ?: throw AccountNotFoundException("Account not found with email $email")
    }

    /**
     * Returns the [Account] found with the *Refresh Token* given.
     *
     * @param refresh the refresh token.
     * @return [Account] found with.
     * @throws UnauthorizedException if the refresh token given did not identify any account.
     */
    fun getByRefreshToken(refresh: String): Account =
        repository.qFindByRefreshToken(refresh)
            ?: throw UnauthorizedException("No account found with refresh token given")

    /**
     * Updates the [account] in the DB with the given [uuid] and sets [Account.uuidBirth] to be
     * [Instant.now].
     *
     * @param account who is getting their uuid set.
     * @param uuid uuid to set.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun updateUuid(account: Account, uuid: UUID?) {
        account.apply {
            this.uuid = uuid
            this.uuidBirth = uuid?.run { Instant.now() }
        }
        repository.save(account)
    }

    /**
     * Updates the [account] in the DB with the given password.
     *
     * @param account account to change status of.
     * @param encryptedPassword encrypted password to change replace the old one.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun updatePassword(account: Account, encryptedPassword: String) {
        account.accountPassword = encryptedPassword
        repository.save(account)
    }

    /**
     * Inserts new [Account].
     *
     * @param username
     * @param email
     * @param encryptedPassword
     * @return [AccountDto] of the inserted [Account]
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun saveAccountWith(username: String, email: String, encryptedPassword: String): Account {

        accountCreationCredentialsValidator.validate(t = username to email)

        val account = Account(username, email, encryptedPassword)

        repository.save(account)

        return account
    }

    /**
     * Hard deletes account with id [id].
     *
     * @param id account identifier, nullable to mitigate cluttering of null checks at usage.
     * @throws AccountNotFoundException if the id given was null.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun deleteById(id: Long?) {
        repository.deleteById(id
            ?: throw AccountNotFoundException("Null account id"))
    }

    /**
     * Updates [AccountStatus] of the given [Account], [account], with [status].
     *
     * @param account account to change status of.
     * @param status which of the statuses change the account's status to.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun updateStatus(account: Account, status: AccountStatus) {
        account.status = status
        repository.save(account)
    }

    /**
     * Updates [Account.refreshToken] of the given [Account], [account], to [refreshToken].
     *
     * @param account account to change refreshToken of.
     * @param refreshToken which token to replace with.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun saveWithRefreshToken(account: Account, refreshToken: String?) {
        account.refreshToken = refreshToken
        repository.save(account)
    }

}