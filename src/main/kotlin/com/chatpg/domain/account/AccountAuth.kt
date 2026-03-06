package com.chatpg.domain.account

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded

@Embeddable
class AccountAuth {

    @Embedded
    var credentials: AccountCredentials = AccountCredentials()

    var a2f: Boolean = false

    var requestHandle: String? = null

    @Column(unique = true)
    var refreshToken: String? = null

}