package com.yourRPG.chatPG.mapper

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.account.AccountDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface AccountMapper {

    fun toDto(account: Account): AccountDto

}