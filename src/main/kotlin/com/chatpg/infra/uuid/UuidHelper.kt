package com.chatpg.infra.uuid

import com.github.f4b6a3.uuid.UuidCreator
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.util.UUID

/**
 * Handles UUIDv7 timestamp.
 */
@Component
class UuidHelper(
    private val clock: Clock
) {

    companion object {
        const val DUMMY_UUID_STRING = "880be27f-655e-467f-a548-8b141bbed1fb"
        val DUMMY_UUID: UUID = UUID.fromString(DUMMY_UUID_STRING)
    }

    /**
     * Using the given UUIDv7's mostSignificantBits, i.e. its leftmost 8 bytes, it is possible to extract the UNIX time
     *  by shifting the microseconds, which takes 2 bytes, out to the right, leaving only the value of time elapsed
     *  since January 1st, 1970 at UTC in milliseconds.
     *
     * * IMPORTANT: This method does not perform check the type of UUID given, it is assumed to be UUIDv7.
     *
     * @param uuidV7 UUID version 7 where the instant shall be extracted from.
     * @return [Long] amount of milliseconds stored in the UUID version 7 given.
     */
    fun getMillis(uuidV7: UUID): Long = uuidV7.mostSignificantBits ushr 2*8

    /**
     * Returns a [Boolean] whether on if the UNIX time contained in the UUIDv7 given after [expirationTime] ago. Just
     *  milliseconds are used for the comparison, the microseconds are discarded by [getMillis].
     *
     * @param uuidV7 UUID version 7 where the timestamp, in milliseconds, will be extracted from for comparison.
     * @return [Boolean] whether on the extracted timestamp in milliseconds is
     */
    fun isNotExpired(uuidV7: UUID, expirationTime: Duration): Boolean =
        getMillis(uuidV7) > clock.millis() - expirationTime.toMillis()

    fun generateUuidV7(): UUID =
        UuidCreator.getTimeOrderedEpoch(clock.instant())

}