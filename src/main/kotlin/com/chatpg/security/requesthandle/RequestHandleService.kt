package com.chatpg.security.requesthandle

import com.github.f4b6a3.uuid.UuidCreator
import com.google.common.hash.Hashing
import com.chatpg.domain.account.Account
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.exception.requesthandle.ExpiredRequestHandleException
import com.chatpg.infra.uuid.UuidHelper
import com.chatpg.logging.LoggingUtils
import com.chatpg.service.account.AccountService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*
import kotlin.random.Random

@Service
class RequestHandleService(
    private val accountService: AccountService,
    private val uuidHelper: UuidHelper,
) {

    private companion object {
        val log = LoggingUtils(this) //TODO implement usage later
    }

    @Transactional
    fun newRequestHandle(
        account: Account,
        subject: RequestHandleSubject
    ): UUID {
        val uuid = UuidCreator.getTimeOrderedEpoch()

        val encodedHandle = hashHandle(uuid, subject)
        accountService.updateRequestHandle(account, encodedHandle)

        return uuid
    }

    @Transactional
    fun newRequestHandleWithCode(
        account: Account,
        subject: RequestHandleSubject,
    ): Pair<UUID, String> {
        val uuid = UuidCreator.getTimeOrderedEpoch()
        val code = newCode()

        val encodedHandle = hashHandle(uuid, subject, code)
        accountService.updateRequestHandle(account, encodedHandle)

        return uuid to code
    }

    @Transactional
    fun getAccountAndDiscardCheckedHandle(
        uuid: UUID,
        subject: RequestHandleSubject,
        expirationTime: Duration,
    ): Account {
        val encodedHandle = hashHandle(uuid, subject)

        val account = getByRequestHandleAndClearElseThrow(encodedHandle)

        validateUuid(uuid, expirationTime, account)
        return account
    }

    @Transactional
    fun getAccountAndDiscardCheckedHandle(
        uuid: UUID,
        subject: RequestHandleSubject,
        code: String,
        expirationTime: Duration,
    ): Account {
        val encodedHandle = hashHandle(uuid, subject, code)

        val account = getByRequestHandleAndClearElseThrow(encodedHandle)

        validateUuid(uuid, expirationTime, account)
        return account
    }

    internal fun getByRequestHandleAndClearElseThrow(encodedHandle: String): Account =
        try {
            accountService.getByRequestHandleAndClear(encodedHandle)
        } catch (_: AccountNotFoundException) {

            throw UnauthorizedException()
        }

    internal fun validateUuid(
        uuid: UUID,
        expirationTime: Duration,
        account: Account,
    ) {
        if (uuid.version() != 7 || !uuidHelper.isNotExpired(uuid, expirationTime)) {
            val id = requireNotNull(account.id)
            throw ExpiredRequestHandleException(accountId = id)
        }
    }

    internal fun newCode(): String =
        Random.nextLong(0L, 1_000_000L)
            .toString()
            .padStart(6, '0')

    internal fun hash(unhashed: String): String = Hashing.sha256()
        .hashString(unhashed, Charsets.UTF_8)
        .toString()

    internal fun hashHandle(
        uuid: UUID,
        subject: RequestHandleSubject
    ): String {
        val unhashed = appendSubject(uuid, subject)
        return hash(unhashed)
    }

    internal fun hashHandle(
        uuid: UUID,
        subject: RequestHandleSubject,
        code: String
    ): String {
        val unhashed = appendSubjectAndCode(uuid, subject, code)
        return hash(unhashed)
    }

    internal fun appendSubject(uuid: UUID, subject: RequestHandleSubject): String =
        "$subject$uuid"

    internal fun appendSubjectAndCode(uuid: UUID, subject: RequestHandleSubject, code: String): String =
        "$subject$uuid$code"

}