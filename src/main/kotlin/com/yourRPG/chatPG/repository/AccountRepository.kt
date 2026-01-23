package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface AccountRepository: JpaRepository<Account, Long> {

    fun findByNameEquals(name: String): Account?

    fun existsByNameEquals(username: String): Boolean

    @Query("SELECT * FROM account WHERE uuid = :uuid", nativeQuery = true)
    fun qFindByUuidEquals(uuid: UUID): Account?

    @Query("SELECT a FROM Account a WHERE a.email = :email")
    fun qFindByEmail(email: String): Account?

    @Query("SELECT CASE WHEN (COUNT(a) > 0) THEN true ELSE false END FROM Account a WHERE a.email = :email")
    fun qExistsByEmail(email: String): Boolean

    @Query("SELECT a FROM Account a WHERE a.refreshToken = :refresh")
    fun qFindByRefreshToken(refresh: String): Account?

}