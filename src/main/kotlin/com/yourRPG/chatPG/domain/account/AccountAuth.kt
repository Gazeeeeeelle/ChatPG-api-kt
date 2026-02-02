package com.yourRPG.chatPG.domain.account

import com.yourRPG.chatPG.service.account.AccountStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Embeddable
class AccountAuth {

    @Embedded
    var credentials: AccountCredentials = AccountCredentials()

    var a2f: Boolean = false

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AccountStatus = AccountStatus.DISABLED

    @Column(unique = true)
    var uuid: UUID? = null

    var uuidBirth: Instant? = null

    @Column(unique = true)
    var refreshToken: String? = null

}