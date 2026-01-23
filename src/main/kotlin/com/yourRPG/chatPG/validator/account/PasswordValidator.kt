package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.exception.auth.password.PasswordContainsIllegalCharactersException
import com.yourRPG.chatPG.exception.auth.password.PasswordDoesNotMeetCharactersOccurrenceCriteriaException
import com.yourRPG.chatPG.exception.auth.password.PasswordTooLongException
import com.yourRPG.chatPG.exception.auth.password.PasswordTooShortException
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class PasswordValidator: IValidatable<String> {

    companion object {
        const val SPECIAL_CHARACTERS = "\\\\\\$\\^\\*\\?\\.\\{\\}\\[\\]\\|@#!%&<>,;:/~=-"
        const val LEGAL_CHARACTERS = "A-Za-z\\d$SPECIAL_CHARACTERS"
        const val LEGAL_CHARACTERS_PATTERN = "^[$LEGAL_CHARACTERS]+$"

        const val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$SPECIAL_CHARACTERS])[$LEGAL_CHARACTERS]{8,255}$"

        val passwordRegex = Regex(PASSWORD_PATTERN)
        val legalCharactersRegex = Regex(LEGAL_CHARACTERS_PATTERN)
    }

    override fun validate(t: String) {

        if (t.length < 8)
            throw PasswordTooShortException("Password should be at least 8 characters long")

        if (t.length > 255)
            throw PasswordTooLongException("Password should be at maximum 255 characters long")

        if (!t.matches(legalCharactersRegex)) {
            throw PasswordContainsIllegalCharactersException("Password contains illegal characters")
        }

        if (!t.matches(passwordRegex)) {
            throw PasswordDoesNotMeetCharactersOccurrenceCriteriaException(
                "Password must contain at least: one upper case and one lower case letter, " +
                        "a number and a special character"
            )
        }

    }

}
