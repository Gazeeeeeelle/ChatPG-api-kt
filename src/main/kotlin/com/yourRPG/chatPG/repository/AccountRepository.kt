package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository: JpaRepository<Account, Long> {

    fun findByNameEquals(name: String): Account?

    fun existsByNameEquals(accountName: String): Boolean

    fun findByUuidEquals(uuid: UUID): Account?

    fun findByEmail(email: String): Account?

}