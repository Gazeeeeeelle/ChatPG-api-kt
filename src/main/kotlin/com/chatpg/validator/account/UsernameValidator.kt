package com.chatpg.validator.account

import com.chatpg.exception.auth.username.UsernameContainsIllegalCharactersException
import com.chatpg.exception.auth.username.UsernameTooLongException
import com.chatpg.exception.auth.username.UsernameTooShortException
import com.chatpg.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class UsernameValidator: IValidatable<String> {

    private companion object {
        val usernameRegex = Regex("^[a-zA-Z0-9_]{3,16}$")
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