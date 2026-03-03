package com.chatpg.dto.account

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AccountDto(

    @field:NotNull
    var publicId: UUID?,

    @field:NotBlank
    var name: String?,

)