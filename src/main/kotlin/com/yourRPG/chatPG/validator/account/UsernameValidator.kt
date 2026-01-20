package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class UsernameValidator: IValidatable<String> {

    companion object {
        const val USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,16}$"
        val usernameRegex = Regex(USERNAME_PATTERN)
    }

    override fun validate(t: String) {
        require(t.length >= 3) {
            "Username length must be greater than or equal to 8"
        }

        require(t.length <= 16) {
            "Username length must be lesser than or equal to 16"
        }

        require(t.matches(usernameRegex)) {
            "Username contains invalid characters. Permitted: a-z, A-Z, 0-9 and _"
        }

    }

}