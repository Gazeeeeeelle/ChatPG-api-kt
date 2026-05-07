package com.chatpg.security.hashing

import com.chatpg.security.requesthandle.RequestHandleSubject
import com.google.common.hash.Hashing
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class HashingService {

    /**
     * Hashes [unhashed] given using SHA256.
     *
     * @param unhashed [String] to be hashed.
     * @return Hashed of the [unhashed] given;
     */
    fun hashWithSha256(unhashed: String): String =
        Hashing
            .sha256()
            .hashString(unhashed, Charsets.UTF_8)
            .toString()

    fun hashHandle(
        uuid: UUID,
        subject: RequestHandleSubject
    ): String {
        val unhashed = uuid.appendSubject(subject)
        return hashWithSha256(unhashed)
    }

    fun hashHandle(
        uuid: UUID,
        subject: RequestHandleSubject,
        code: String
    ): String {
        val unhashed = uuid.appendSubjectAndCode(subject, code)
        return hashWithSha256(unhashed)
    }

    internal fun (UUID).appendSubject(subject: RequestHandleSubject): String =
        "$subject$this"

    internal fun (UUID).appendSubjectAndCode(subject: RequestHandleSubject, code: String): String =
        "$subject$this$code"

}