package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.account.CreateAccountDto
import com.yourRPG.chatPG.exception.http.ConflictException
import com.yourRPG.chatPG.exception.auth.AccountActivationException
import com.yourRPG.chatPG.helper.email.MimeHelper
import com.yourRPG.chatPG.helper.uri.FrontendUriHelper
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.account.AccountStatus
import com.yourRPG.chatPG.service.email.EmailService
import com.yourRPG.chatPG.validator.account.PasswordValidator
import com.yourRPG.chatPG.validator.account.UsernameValidator
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class AuthCreateAccountService(
    private val passwordService: AuthPasswordService,
    private val accountService: AccountService,
    private val emailService: EmailService,

    private val usernameValidator: UsernameValidator,
    private val passwordValidator: PasswordValidator,

    private val mimeHelper: MimeHelper,
    private val frontendUriHelper: FrontendUriHelper
) {

    @Modifying
    @Transactional
    fun createAccount(dto: CreateAccountDto): AccountDto {

        usernameValidator.validate(t = dto.username)
        passwordValidator.validate(t = dto.password)

        val encryptedPassword = passwordService.encrypt(rawPassword = dto.password)

        val account = accountService.saveAccountWith(dto.username, dto.email, encryptedPassword)

        val uuid = UUID.randomUUID()

        accountService.updateUuid(account, uuid)

        val html = mimeHelper.getTemplate(
            template  = "mime-activate-account",
            variables = arrayOf(
                "url" to frontendUriHelper.append("/login/activate-account/$uuid")
            )
        )

        emailService.sendMimeEmail(
            subject = "Activate account",
            to = dto.email,
            html
        )

        return accountService.dtoOf(c = account)
    }

    @Modifying
    @Transactional
    fun activateAccount(dto: UuidDto): AccountDto {
        val account: Account = accountService.getByUuid(UUID.fromString(dto.uuid)) //DELEGATION

        accountService.updateUuid(account, null) //DELEGATION

        when (account.status) {
            AccountStatus.DISABLED -> {}
            AccountStatus.ENABLED  -> throw ConflictException("Account already activated")
            AccountStatus.DELETED  -> throw ConflictException("This account is deleted")
        }

        val birth = account.uuidBirth
            ?: throw AccountActivationException("Uuid does not have instant of creation")

        val thirtyMinutesAgo = Instant.now()
            .minus(30, ChronoUnit.MINUTES)

        if (birth.isAfter(thirtyMinutesAgo)) {
            accountService.updateStatus(account, status = AccountStatus.ENABLED) //DELEGATION
            return accountService.dtoOf(c = account) //DELEGATION & OUTCOME
        }

        accountService.deleteById(account.id)

        throw AccountActivationException("Request expired. Request will be deleted") //OUTCOME
    }

}