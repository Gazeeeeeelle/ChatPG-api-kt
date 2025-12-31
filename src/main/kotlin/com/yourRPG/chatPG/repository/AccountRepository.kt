package com.yourRPG.chatPG.repository

import com.yourRPG.chatPG.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository: JpaRepository<Account, Long> {

    fun findByNameEquals(name: String): Account?

}