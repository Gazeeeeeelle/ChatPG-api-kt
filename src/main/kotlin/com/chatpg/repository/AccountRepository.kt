package com.chatpg.repository

import com.chatpg.domain.account.Account
import com.chatpg.service.account.AccountStatus
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
    fun qUpdateRequestHandle(id: Long, encodedHandle: String): Int

    @Modifying
    @Query("UPDATE Account a SET a.status = :status WHERE a.id = :id")
    fun qUpdateStatus(id: Long, status: AccountStatus): Int

    @Modifying
    @Query("UPDATE Account a SET a.auth.requestHandle = null WHERE a.id = :id")
    fun qRemoveHandleById(id: Long): Int

    @Modifying
    @Query("UPDATE Account a SET a.auth.refreshToken = :refreshToken WHERE a.id = :id")
    fun qUpdateRefreshToken(id: Long, refreshToken: String?): Int

    @Modifying
    @Query("UPDATE Account a SET a.auth.credentials.password = :encodedPassword WHERE a.id = :id")
    fun qUpdateEncodedPassword(id: Long, encodedPassword: String): Int

    @Modifying
    @Query("DELETE Account a WHERE a.id = :id")
    fun qDeleteById(id: Long): Int

}