package com.chatpg.service.account

import com.chatpg.domain.account.Account
import com.chatpg.dto.account.AccountDto
import com.chatpg.exception.account.AccountIdNotFoundException
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.logging.LoggingUtils
import com.chatpg.mapper.AccountMapper
import com.chatpg.repository.AccountRepository
import com.chatpg.validator.account.AccountCreationCredentialsValidator
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

    private companion object {
        val log = LoggingUtils.logger {}
    }

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
            ?: log.andThrow {
                AccountNotFoundException("Not found with ID given")
            }

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
            ?: log.andThrow {
                AccountNotFoundException("Account not found with name given")
            }

    /**
     * Returns the [Account] found with [publicId].
     *
     * @param publicId
     * @return Account found with matching [UUID]s.
     * @throws AccountNotFoundException if no account was found with [UUID] [publicId]
     */
    fun getByPublicId(publicId: UUID): Account =
        repository.qFindByPublicId(publicId)
            ?: log.andThrow {
                AccountNotFoundException("Account not found with Public ID given")
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
            ?: log.andThrow {
                AccountNotFoundException("Not found with email given")
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
            ?: log.andThrow  {
                AccountNotFoundException("Not found with Refresh Token given")
            }

    /**
     * Inserts new [Account].
     *
     * @param unpersistedAccount account to validate and persist under successful validation.
     * @return Persisted [Account].
     */
    @Transactional
    fun insertAccount(unpersistedAccount: Account): Account {
        accountCreationCredentialsValidator.validate(unpersistedAccount)

        return repository.save(unpersistedAccount)
    }

    /**
     * Returns the [Account] found with its [com.chatpg.domain.account.AccountAuth.requestHandle].
     *
     * @param encodedHandle used to identify request.
     * @return Account found with request handle given.
     * @throws AccountNotFoundException if no account was identified with the given request handle.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun getByRequestHandleAndClear(encodedHandle: String): Account {
        val account = repository.qFindByRequestHandle(encodedHandle)
            ?: log.andThrow {
                AccountNotFoundException("Not found with Request Handle given")
            }

        val id = requireNotNullAccountId(account)

        account.auth.requestHandle = null

        repository.qRemoveHandleById(id)
            .takeUnless { it == 0 }
            ?: log.andThrow {
                AccountNotFoundException("Not found with ID given when updating Request Handle")
            }

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
        val id = requireNotNullAccountId(account)

        account.auth.requestHandle = encodedHandle

        repository.qUpdateRequestHandle(id, encodedHandle)
            .takeUnless { it == 0 }
            ?: log.andThrow {
                AccountNotFoundException("Not found with ID given when updating Request Handle")
            }
    }

    /**
     * Updates the [account] in the DB with the given password.
     *
     * @param account account to change status of.
     * @param encodedPassword encrypted password to change replace the old one.
     * @throws AccountNotFoundException If no deletes were made on call [AccountRepository.qUpdateRefreshToken]
     */
    @Transactional
    fun updatePassword(account: Account, encodedPassword: String) {
        account.auth.credentials.password = encodedPassword

        val id = requireNotNullAccountId(account)

        repository.qUpdateEncodedPassword(id, encodedPassword)
            .takeUnless { it == 0 }
            ?: log.andThrow {
                AccountNotFoundException("Not found with ID given when updating Request Handle")
            }
    }

    /**
     * Hard deletes account found with id [id].
     *
     * @param id account identifier.
     * @throws AccountNotFoundException If no deletes were made on call [AccountRepository.qUpdateRefreshToken]
     */
    @Transactional
    fun deleteById(id: Long) =
        repository.qDeleteById(id)
            .takeUnless { it == 0 }
            ?: log.andThrow {
                AccountNotFoundException("Not found with ID given when updating Request Handle")
            }

    /**
     * Updates [AccountStatus] of the given [Account], [account], with [status].
     *
     * @param account account to change status of.
     * @param status which of the statuses change the account's status to.
     * @throws AccountNotFoundException If no updates were made on call [AccountRepository.qUpdateRefreshToken]
     */
    @Transactional
    fun updateStatus(account: Account, status: AccountStatus) {
        val id = requireNotNullAccountId(account)

        account.status = status

        repository.qUpdateStatus(id, status)
            .takeUnless { it == 0 }
            ?: log.andThrow {
                AccountNotFoundException("Not found with ID given when updating Request Handle")
            }
    }

    /**
     * Updates [com.chatpg.domain.account.AccountAuth.refreshToken] of the given [Account], [account], to
     *  [refreshToken].
     *
     * @param account account to change refreshToken of.
     * @param refreshToken which token to replace with.
     * @throws AccountNotFoundException If no updates were made on call [AccountRepository.qUpdateRefreshToken]
     */
    @Transactional
    fun updateRefreshToken(account: Account, refreshToken: String?) {
        account.auth.refreshToken = refreshToken

        val id = requireNotNullAccountId(account)
        repository.qUpdateRefreshToken(id, refreshToken)
            .takeUnless { it == 0 }
            ?: log.andThrow {
                AccountNotFoundException("Not found with ID given when updating Request Handle")
            }
    }

    /**
     * Returns account's non-null asserted ID, or else, trows [AccountIdNotFoundException].
     *
     * @param account Account that will have its ID null checked.
     * @return [account]'s non-null ID.
     * @throws AccountIdNotFoundException if [account]'s ID was null.
     */
    internal fun requireNotNullAccountId(account: Account): Long =
        account.id
            ?: log.andThrow {
                AccountIdNotFoundException()
            }

}
