package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class PasswordValidator: IValidatable<String> {

    final val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,255}$")

    final val banned = listOf(
        "P@ssw0rd1", "Admin@123", "Welcome!1",
        "Qwert!23", "Password#1", "1qaz@WSX",
        "!QAZ2wsx", "1234!Aa", "!@#123Aa"
    )

    override fun validate(t: String) {

        require(t.length >= 8) {
            "Password length must be greater than 8"
        }

        require(t.matches(regex)) {
            "Password contains invalid characters"
        }

        require(!banned.contains(t)) {
            "Password is too simple"
        }

    }

}