package com.yourRPG.chatPG.dto.account

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AccountDto(

    @field:NotNull
    var publicId: Long?,

    @field:NotBlank
    var name: String?,

)