package com.chatpg.exception.account

import com.chatpg.exception.http.HttpException
import org.slf4j.event.Level

class AccountIdNotFoundException: HttpException(
    404,
    "Account ID not found",
    Level.ERROR,
    "Inconsistency with database constraint 'NOT NULL' for column 'id' on table 'account'"
)