package com.yourRPG.chatPG.security.requesthandle

import com.github.f4b6a3.uuid.UuidCreator
import com.google.common.hash.Hashing
import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import com.yourRPG.chatPG.infra.uuid.UuidHelper
import com.yourRPG.chatPG.service.account.AccountService
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

    fun getAccountAndDiscardCheckedHandle(
        uuid: UUID,
        subject: RequestHandleSubject,
        expirationTime: Duration,
    ): Account {
        val account = getAccountByCheckedRequestHandle(uuid, subject, expirationTime)
        accountService.removeHandle(account)
        return account
    }

    internal fun getAccountByCheckedRequestHandle(
        uuid: UUID,
        subject: RequestHandleSubject,
        expirationTime: Duration,
    ): Account {
        uuidHelper.assertVersion(uuid, version = 7)

        val encodedHandle = hashHandle(uuid, subject)

        return if (uuidHelper.isNotExpired(uuid, expirationTime)) {
            accountService.getByRequestHandle(encodedHandle)
        } else {
            clearRequestHandle(encodedHandle)
            throw UnauthorizedException("Request expired")
        }
    }

    internal fun newCode(): String =
        Random.nextLong(0L, 1_000_000L).toString()

    internal fun clearRequestHandle(encodedHandle: String) {
        accountService.removeHandle(encodedHandle)
    }

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