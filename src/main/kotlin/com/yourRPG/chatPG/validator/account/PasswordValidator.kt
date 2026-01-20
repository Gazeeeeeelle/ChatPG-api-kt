package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class PasswordValidator: IValidatable<String> {

    companion object {
        const val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!%*?&])[A-Za-z\\d@#$!%*?&]{8,255}$"
        const val LEGAL_CHARACTERS_PATTERN = "[A-Za-z\\d@#$!%*?&]+"
        val passwordRegex = Regex(PASSWORD_PATTERN)
        val legalCharactersRegex = Regex(LEGAL_CHARACTERS_PATTERN)
    }

    override fun validate(t: String) {

        require(t.length >= 8) {
            "Password length must be greater than or equal to 8"
        }

        require(t.length <= 255) {
            "Password length must be lesser than or equal to 255"
        }

        require(t.matches(legalCharactersRegex)) {
            "Password contains illegal characters"
        }

        require(t.matches(passwordRegex)) {
            "Password must contain at least: one upper case and one lower case letter, a number and a special character"
        }

    }

}
