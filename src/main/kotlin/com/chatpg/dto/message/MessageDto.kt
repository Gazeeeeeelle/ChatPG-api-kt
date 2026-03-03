package com.chatpg.dto.message

import com.chatpg.dto.account.AccountDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MessageDto(

    @field: NotNull
    val id: Long?,

    @field:NotBlank
    val content: String,

    @field: NotNull
    val bot: Boolean,

    @field: NotNull
    val account: AccountDto?

)
