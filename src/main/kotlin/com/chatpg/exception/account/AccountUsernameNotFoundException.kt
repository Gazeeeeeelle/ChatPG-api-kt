package com.chatpg.exception.account

import com.chatpg.exception.http.FullDetailHttpException
import org.slf4j.event.Level

class AccountUsernameNotFoundException: FullDetailHttpException (
    status          = 404,
    message         = "Account username not found",
    level           = Level.ERROR,
    internalMessage = "Inconsistency with database constraint 'NOT NULL' for column 'name' on table 'account'"
)