package com.yourRPG.chatPG.dto.account

import com.yourRPG.chatPG.model.Account
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AccountDto(

    @field: NotNull
    var id: Long?,

    @field: NotBlank
    var name: String?,

) {

    constructor(account: Account): this(account.id, account.name)

}
