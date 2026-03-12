package com.chatpg.exception.account

import com.chatpg.exception.http.FullDetailHttpException
import org.slf4j.event.Level

class AccountPasswordNotFoundException: FullDetailHttpException(
    status          = 404,
    message         = "Account password not found",
    level           = Level.ERROR,
    internalMessage = "Inconsistency with database constraint 'NOT NULL' for column 'password' on table 'account'"
)
