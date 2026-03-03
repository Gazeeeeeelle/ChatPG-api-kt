package com.chatpg.exception.account

import com.chatpg.exception.http.HttpException
import org.slf4j.event.Level

class AccountPasswordNotFoundException: HttpException(
    404,
    "Account password not found",
    Level.ERROR,
    "Inconsistency with database constraint 'NOT NULL' for column 'password' on table 'account'"
)
