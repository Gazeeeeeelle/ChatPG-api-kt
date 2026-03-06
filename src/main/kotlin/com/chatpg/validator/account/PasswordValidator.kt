package com.chatpg.validator.account

import com.chatpg.exception.auth.password.PasswordContainsIllegalCharactersException
import com.chatpg.exception.auth.password.PasswordDoesNotMeetCharactersOccurrenceCriteriaException
import com.chatpg.exception.auth.password.PasswordTooLongException
import com.chatpg.exception.auth.password.PasswordTooShortException
import com.chatpg.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class PasswordValidator: IValidatable<String> {

    private companion object {
        const val SPECIAL_CHARACTERS = """\$\^\*\?\.\{\}\[\]\|\\@#!%&<>,;:/~=-"""
        const val LEGAL_CHARACTERS_SET = """[A-Za-z\d$SPECIAL_CHARACTERS]"""

        val passwordRegex =
            Regex(pattern =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$SPECIAL_CHARACTERS])$LEGAL_CHARACTERS_SET{8,255}$"
            )
        val legalCharactersRegex = Regex(pattern = "^[A-Za-z\\d$SPECIAL_CHARACTERS]+$")
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
