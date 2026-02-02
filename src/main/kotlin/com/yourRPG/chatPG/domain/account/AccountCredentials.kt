package com.yourRPG.chatPG.domain.account

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.Email
import javax.validation.constraints.NotNull

@Embeddable
class AccountCredentials {

    @Email
    @Column(unique = true, nullable = false)
    var email: String? = null

    @NotNull
    @Column(name = "password")
    var accountPassword: String? = null

}