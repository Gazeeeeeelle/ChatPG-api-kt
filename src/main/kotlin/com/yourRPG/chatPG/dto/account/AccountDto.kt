package com.yourRPG.chatPG.dto.account

import com.yourRPG.chatPG.model.Account
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

data class AccountDto(
    @Id @GeneratedValue var id: Long? = null,
    var name: String? = null,
) {

    constructor(account: Account): this(account.getId(), account.getName())

}
