package com.chatpg.mapper

import com.chatpg.domain.account.Account
import com.chatpg.dto.account.AccountDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface AccountMapper {

    fun toDto(account: Account): AccountDto

}