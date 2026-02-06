package com.yourRPG.chatPG.infra.uuid

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * Handles UUIDv7 timestamp.
 */
@Component
class UuidHelper(
    private val clock: Clock
) {

    /**
     * Using the given UUIDv7's mostSignificantBits, i.e. its leftmost 8 bytes, it is possible to extract the UNIX time
     *  by shifting the microseconds, which takes 2 bytes, out to the right, leaving only the value of time elapsed
     *  since January 1st, 1970 at UTC in milliseconds.
     *
     * * FIXME: any uuid given is treated as UUIDv7.
     *
     * @param uuidV7 UUID version 7 where the instant shall be extracted from.
     * @return [Long] amount of milliseconds stored in the UUID version 7 given.
     */
    fun getMillis(uuidV7: UUID): Long = uuidV7.mostSignificantBits ushr 2*8

    /**
     * Using [getMillis], the unix time in milliseconds is extracted from the UUIDv7 given and then used to return an
     *  equivalent [Instant].
     *
     * @param uuidV7 UUID version 7 where the instant shall be extracted from.
     * @return [Instant] stored in the UUID version 7 given.
     */
    fun getInstant(uuidV7: UUID): Instant = Instant.ofEpochMilli(getMillis(uuidV7))

    /**
     * Returns a [Boolean] whether on if the UNIX time contained in the UUIDv7 given after [expirationTime] ago. Just
     *  milliseconds are used for the comparison, the microseconds are discarded by [getMillis].
     *
     * @param uuidV7 UUID version 7 where the timestamp, in milliseconds, will be extracted from for comparison.
     * @return [Boolean] whether on the extracted timestamp in milliseconds is
     */
    fun isNotExpired(uuidV7: UUID, expirationTime: Duration): Boolean =
        getMillis(uuidV7) > clock.millis() - expirationTime.toMillis()

    /**
     * TODO: documentation and revise purpose.
     */
    fun assertVersion(uuid: UUID, version: Int) = assert(uuid.version() == version)

}