package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface AccountRepository: JpaRepository<Account, Long> {

    fun findByNameEquals(name: String): Account?

    fun existsByNameEquals(username: String): Boolean

    fun findByUuidEquals(uuid: UUID): Account?

    fun findByEmail(email: String): Account?

    @Query("SELECT CASE WHEN (COUNT(a) > 0) THEN true ELSE false END FROM Account a WHERE a.email = :email")
    fun qExistsByEmail(email: String): Boolean

}