package com.chatpg.exception.account

import com.chatpg.exception.http.FullDetailHttpException
import org.slf4j.event.Level

class AccountIdNotFoundException: FullDetailHttpException(
    status          = 404,
    message         = "Account ID not found",
    level           = Level.ERROR,
    internalMessage = "Inconsistency with database constraint 'NOT NULL' for column 'id' on table 'account'"
)