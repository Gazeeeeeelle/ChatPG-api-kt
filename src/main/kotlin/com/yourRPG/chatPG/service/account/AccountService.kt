package com.yourRPG.chatPG.service.account

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.mapper.AccountMapper
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.validator.account.AccountCreationCredentialsValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AccountService(
    private val repository: AccountRepository,
    private val mapper: AccountMapper,

    private val accountCreationCredentialsValidator: AccountCreationCredentialsValidator
) {

    private companion object val log = KotlinLogging.logger {}

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
            ?: throwAccountNotFoundException("Account not found with given")

    /**
     * Delegates fetching of Account to [getById] and then converts it to DTO.
     *
     * @param accountId
     * @return [AccountDto] of the identified [Account]
     *
     * @see getById
     */
    fun getDtoById(accountId: Long): AccountDto =
        mapper.toDto(getById(accountId))

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
            ?: throwAccountNotFoundException("Account not found with name given")

    /**
     * Returns the [Account] found with [publicId].
     *
     * @param publicId
     * @return Account found with matching [UUID]s.
     * @throws AccountNotFoundException if no account was found with [UUID] [publicId]
     */
    fun getByPublicId(publicId: UUID): Account =
        repository.qFindByPublicId(publicId)
            ?: run {
                log.warn { "No account found with public ID given" }
                throwAccountNotFoundException("No account found with public ID given")
            }

    /**
     * Returns the Account found with [email] given.
     *
     * @param email used to search for the account.
     * @return [Account] found.
     * @throws AccountNotFoundException
     */
    fun getByEmail(email: String): Account =
        repository.qFindByEmail(email)
            ?: throwAccountNotFoundException("Account not found with email $email")

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
     * Returns the [Account] found with its [com.yourRPG.chatPG.domain.account.AccountAuth.requestHandle].
     *
     * @param encodedHandle used to identify request.
     * @return Account found with request handle given.
     * @throws AccountNotFoundException if no account was identified with the given request handle.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun getByRequestHandleAndClear(
        encodedHandle: String,
    ): Account {
        val account = repository.qFindByRequestHandle(encodedHandle)
            ?: throw AccountNotFoundException("Account not found with given")

        val id = requireNotNull(account.id) { "Account id can not be null" }

        repository.qRemoveHandleById(id)
        return account
    }

    /**
     * Updates the [account] in the DB with the given [encodedHandle].
     *
     * @param account entity getting their [encodedHandle] changed.
     * @param encodedHandle what to change to.
     */
    @Transactional
    fun updateRequestHandle(account: Account, encodedHandle: String) {
        val id = requireNotNull(account.id) { "Account id is null." }

        account.auth.requestHandle = encodedHandle
        repository.qUpdateRequestHandle(id, encodedHandle)
    }

    /**
     * Updates the [account] in the DB with the given password.
     *
     * @param account account to change status of.
     * @param encodedPassword encrypted password to change replace the old one.
     */
    @Transactional
    fun updatePassword(account: Account, encodedPassword: String) {
        account.auth.credentials.password = encodedPassword
        repository.save(account)
    }

    /**
     * Inserts new [Account].
     *
     * @param username
     * @param email
     * @param encodedPassword
     * @return [AccountDto] of the inserted [Account]
     */
    @Transactional
    fun insertAccount(username: String, email: String, encodedPassword: String): Account {
        accountCreationCredentialsValidator.validate(t = username to email)

        val account = Account(username, email, encodedPassword)
        return repository.save(account)
    }

    /**
     * Hard deletes account found with id [id].
     *
     * @param id account identifier.
     */
    @Transactional
    fun deleteById(id: Long) = repository.deleteById(id)

    /**
     * Updates [AccountStatus] of the given [Account], [account], with [status].
     *
     * @param account account to change status of.
     * @param status which of the statuses change the account's status to.
     */
    @Transactional
    fun updateStatus(account: Account, status: AccountStatus) {
        val id = requireNotNull(account.id) { "Account id is null." }

        account.status = status
        repository.qUpdateStatus(id, status)
    }

    /**
     * Updates [com.yourRPG.chatPG.domain.account.AccountAuth.refreshToken] of the given [Account], [account], to
     *  [refreshToken].
     *
     * @param account account to change refreshToken of.
     * @param refreshToken which token to replace with.
     */
    @Transactional
    fun updateRefreshToken(account: Account, refreshToken: String?) {
        account.auth.refreshToken = refreshToken
        repository.save(account)
    }

    internal fun throwAccountNotFoundException(message: String): Nothing {
        log.warn { message }
        throw AccountNotFoundException(message)
    }

}