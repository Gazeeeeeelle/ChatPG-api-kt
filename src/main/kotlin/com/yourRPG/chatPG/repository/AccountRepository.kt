package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.service.account.AccountStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface AccountRepository: JpaRepository<Account, Long> {

    fun findByNameEquals(name: String): Account?

    fun existsByNameEquals(username: String): Boolean

    @Query("SELECT * FROM account WHERE public_id = :publicId", nativeQuery = true)
    fun qFindByPublicId(publicId: UUID): Account?

    @Query("SELECT a FROM Account a WHERE a.auth.credentials.email = :email")
    fun qFindByEmail(email: String): Account?

    @Query("SELECT CASE WHEN (COUNT(a) > 0) THEN true ELSE false END FROM Account a WHERE a.auth.credentials.email = :email")
    fun qExistsByEmail(email: String): Boolean

    @Query("SELECT a FROM Account a WHERE a.auth.refreshToken = :refresh")
    fun qFindByRefreshToken(refresh: String): Account?

    @Query("SELECT * FROM account WHERE request_handle = :encodedHandle", nativeQuery = true)
    fun qFindByRequestHandle(encodedHandle: String): Account?

    @Modifying
    @Query("UPDATE Account a SET a.auth.requestHandle = :encodedHandle WHERE a.id = :id")
    fun qUpdateRequestHandle(id: Long, encodedHandle: String)

    @Modifying
    @Query("UPDATE Account a SET a.status = :status WHERE a.id = :id")
    fun qUpdateStatus(id: Long, status: AccountStatus)

    @Modifying
    @Query("UPDATE Account a SET a.auth.requestHandle = null WHERE a.requestHandle = :requestHandle")
    fun qRemoveHandle(encodedHandle: String)

    @Modifying
    @Query("UPDATE Account a SET a.auth.requestHandle = null WHERE a.id = :id")
    fun qRemoveHandleById(id: Long)

}