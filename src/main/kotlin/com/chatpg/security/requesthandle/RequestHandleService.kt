package com.chatpg.security.requesthandle

import com.chatpg.domain.account.Account
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.exception.requesthandle.ExpiredRequestHandleException
import com.chatpg.infra.uuid.UuidHelper
import com.chatpg.logging.LoggingUtils
import com.chatpg.security.hashing.HashingService
import com.chatpg.service.account.AccountService
import com.github.f4b6a3.uuid.UuidCreator
import org.slf4j.event.Level
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*
import kotlin.math.ceil
import kotlin.random.Random

@Service
class RequestHandleService(
    private val accountService: AccountService,
    private val uuidHelper: UuidHelper,
    private val hashingService: HashingService
) {

    private companion object {
        val log = LoggingUtils.logger {}
        const val EIGHTY_PERCENT = .80
    }

    /**
     * Creates new request handle by hashing the string of an UUIDv7 and the subject of the request.
     * Account's respective request handle is updated to the generated one.
     *
     * @param account [Account] that will have its Request Handle updated.
     * @param subject [RequestHandleSubject], matter of the Request Handle.
     * @return UUIDv7 generated.
     */
    @Transactional
    fun newRequestHandle(
        account: Account,
        subject: RequestHandleSubject
    ): UUID {
        val uuid = uuidHelper.generateUuidV7()

        val encodedHandle = hashingService.hashHandle(uuid, subject)
        accountService.updateRequestHandle(account, encodedHandle)

        log.at(Level.INFO) {
            "Created new request handle with subject $subject"
        }
        return uuid
    }

    /**
     * Creates new request handle by hashing the string of an UUIDv7, a 6-digit random code, and the subject of the
     *  request. The hash process in dictated by [HashingService.hashHandle].
     * Account's respective request handle is updated to the generated one.
     *
     * @param account [Account] that will have its Request Handle updated.
     * @param subject [RequestHandleSubject], matter of the Request Handle.
     * @return [Pair] of UUIDv7 and the 6-digit code generated.
     */
    @Transactional
    fun newRequestHandleWithCode(
        account: Account,
        subject: RequestHandleSubject,
    ): Pair<UUID, String> {
        val uuid = UuidCreator.getTimeOrderedEpoch()
        val code = newCode()

        val encodedHandle = hashingService.hashHandle(uuid, subject, code)
        accountService.updateRequestHandle(account, encodedHandle)

        log.at(Level.INFO) {
            "Created new request handle with subject $subject" +
                    "and code ${code.obscurate(EIGHTY_PERCENT)}"
        }
        return uuid to code
    }

    /**
     * Obscurates contiguously a percentage ([percentageOfText]) of the text given by replacing its last nth characters
     *  by 'x'.
     * The calculates amount of characters to hashed is always rounded up to the next integer.
     * This means that for a [String] of length 1, if [percentageOfText] is, for example, 0.01 (1%), all the string will
     *  be hashed, since
     *
     * `1[length of the text] * 0.01[percentageOfText] = 0.01`,
     *
     * but is rounded up to 1 with [ceil].
     *
     * `Amount of characters hashed = ceil(length of text * [percentageOfText])`.
     *
     * Another example: `"bread".obscurate(0.50)` will result in `"brxxx"`.
     *
     * @param percentageOfText Percentage of text to be hashed (Rounded up).
     * @return hashed [String].
     */
    internal fun (String).obscurate(percentageOfText: Double): String {
        val toBeObscurated: Int = ceil(percentageOfText * this.length).toInt()

        return this
            .dropLast(n = toBeObscurated)
            .padEnd(length = toBeObscurated, 'x')
    }

    /**
     * With the UUIDv7 given, checks if its creation is older than the [expirationTime], throwing
     * Identifies an [Account] by its hashed request handle, following the process of hashing defined in [HashingService.hashHandle],
     *  then sets the requestHandle of the [Account] to `null` in the database.
     *
     * @param uuid [UUID] generated when the Request Handle identifying the account was created.
     * @param subject [RequestHandleSubject], matter of the Request Handle.
     * @param expirationTime
     */
    @Transactional
    fun getAccountAndDiscardCheckedHandle(
        uuid: UUID,
        subject: RequestHandleSubject,
        expirationTime: Duration,
    ): Account {
        val encodedHandle = hashingService.hashHandle(uuid, subject)

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
        val encodedHandle = hashingService.hashHandle(uuid, subject, code)

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

}