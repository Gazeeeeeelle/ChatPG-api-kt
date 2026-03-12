package com.chatpg.validator.account

import com.chatpg.domain.account.Account
import com.chatpg.exception.auth.username.UsernameAlreadyRegisteredException
import com.chatpg.exception.email.EmailAlreadyRegisteredException
import com.chatpg.exception.http.sc4xx.BadRequestException
import com.chatpg.repository.AccountRepository
import com.chatpg.validator.IValidatable
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class AccountCreationCredentialsValidator(
    private val repository: AccountRepository,
): IValidatable<Account> {

    private companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Checks if Username or Email clashes with registered accounts in the database.
     *
     * @param t [Account] to check Username and Email.
     * @throws BadRequestException if either Username or Email are null, which should not happen given the previous
     *  validations, and is therefore logged.
     * @throws UsernameAlreadyRegisteredException if a persisted account was found with the same Username.
     * @throws EmailAlreadyRegisteredException if a persisted account was found with the same Email.
     */
    override fun validate(t: Account) {
        val name = t.name ?: run {
            log.warn { "Null name reached validator" }
            throw BadRequestException("Name cannot be null")
        }

        val email = t.auth.credentials.email ?: run {
            log.warn { "Null email reached validator" }
            throw BadRequestException("Email cannot be null")
        }

        if (repository.existsByNameEquals(name))
            throw UsernameAlreadyRegisteredException("There already is an account with that username")

        if (repository.qExistsByEmail(email))
            throw EmailAlreadyRegisteredException("There already is an account with that email")
    }

}