package com.yourRPG.chatPG.validator.account

import com.yourRPG.chatPG.exception.ConflictException
import com.yourRPG.chatPG.repository.AccountRepository
import com.yourRPG.chatPG.validator.IValidatable
import org.springframework.stereotype.Component

@Component
class AccountCreationCredentialsValidator(
    private val repository: AccountRepository,
): IValidatable<Pair<String, String>> {

    override fun validate(t: Pair<String, String>) {
        val (username, email) = t

        if (repository.existsByNameEquals(username))
            throw ConflictException("There already is an account with that username")

        if (repository.qExistsByEmail(email))
            throw ConflictException("There already is an account with that email")

    }

}