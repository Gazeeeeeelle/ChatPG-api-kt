package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.dto.account.AccountDto
import com.yourRPG.chatPG.dto.auth.UuidDto
import com.yourRPG.chatPG.dto.auth.OpenAccountCreationDto
import com.yourRPG.chatPG.exception.auth.AccountActivationException
import com.yourRPG.chatPG.exception.http.ConflictException
import com.yourRPG.chatPG.infra.email.EmailService
import com.yourRPG.chatPG.infra.email.MimeHelper
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.infra.uuid.UuidHelper
import com.yourRPG.chatPG.mapper.AccountMapper
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleSubject
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.account.AccountStatus
import com.yourRPG.chatPG.validator.account.PasswordValidator
import com.yourRPG.chatPG.validator.account.UsernameValidator
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class AuthCreateAccountService(
    private val passwordEncoder: PasswordEncoder,
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val requestHandleService: RequestHandleService,

    private val usernameValidator: UsernameValidator,
    private val passwordValidator: PasswordValidator,

    private val mimeHelper: MimeHelper,
    private val frontendUriHelper: FrontendUriHelper,
    private val uuidHelper: UuidHelper,

    private val accountMapper: AccountMapper,

    @param:Value("\${security.request-handle.activate-account-expires-in}")
    private val activateAccountExpiresIn: Duration,

    @param:Value("\${frontend.paths.activate-account}")
    private val activateAccountUrl: String,
) {

    companion object {
        private val subject = RequestHandleSubject.ACTIVATE_ACCOUNT
    }

    @Transactional
    fun openAccountCreation(dto: OpenAccountCreationDto): AccountDto {
        usernameValidator.validate(t = dto.username)
        passwordValidator.validate(t = dto.password)

        val encryptedPassword = passwordEncoder.encode(dto.password)

        val account = accountService.insertAccount(dto.username, dto.email, encryptedPassword)

        val handle = requestHandleService.newRequestHandle(
            account,
            subject
        )

        val html = mimeHelper.getTemplate(
            template = "mime-activate-account",
            "url" to frontendUriHelper.appendString("$activateAccountUrl?uuid=$handle")
        )

        emailService.sendMimeEmail(
            subject = "Activate account",
            to = dto.email,
            html
        )

        return accountMapper.toDto(account)
    }

    @Transactional(dontRollbackOn = [AccountActivationException::class])
    fun fulfillAccountCreation(dto: UuidDto): AccountDto {
        val requestHandle = UUID.fromString(dto.uuid)
        val account: Account =
            requestHandleService.getAccountByCheckedRequestHandle(
                requestHandle,
                subject,
                activateAccountExpiresIn
            )

        when (account.status) {
            AccountStatus.DISABLED -> {}
            AccountStatus.ENABLED  -> throw ConflictException("Account already activated")
            AccountStatus.DELETED  -> throw ConflictException("This account is deleted")
        }

        accountService.removeHandle(account)

        if (uuidHelper.isNotExpired(uuidV7 = requestHandle, expirationTime = activateAccountExpiresIn)) {
            accountService.updateStatus(account, status = AccountStatus.ENABLED)
            return accountMapper.toDto(account)
        }

        accountService.deleteById(account.id)
        throw AccountActivationException("Request expired. Request will be deleted. ")
    }

}