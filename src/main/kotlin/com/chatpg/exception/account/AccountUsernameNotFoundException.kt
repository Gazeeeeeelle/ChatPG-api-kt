package com.chatpg.exception.account

import com.chatpg.exception.http.HttpException
import org.slf4j.event.Level

class AccountUsernameNotFoundException: HttpException(
    404,
    "Account username not found",
    Level.ERROR,
    internalMessage = "Inconsistency with database constraint 'NOT NULL' for column 'name' on table 'account'"
)