package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.exception.auth.username.UsernameContainsIllegalCharactersException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooLongException
import com.yourRPG.chatPG.exception.auth.username.UsernameTooShortException
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class UsernameValidator: IValidatable<String> {

    companion object {
        const val USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,16}$"
        val usernameRegex = Regex(USERNAME_PATTERN)
    }

    override fun validate(t: String) {

        if (t.length < 3)
            throw UsernameTooShortException("Username length must be at least 3")

        if (t.length > 16)
            throw UsernameTooLongException("Username length must be at maximum 16")

        if (!t.matches(usernameRegex))
            throw UsernameContainsIllegalCharactersException("Username contains invalid characters. Permitted: a-z, A-Z, 0-9 and _")

    }

}