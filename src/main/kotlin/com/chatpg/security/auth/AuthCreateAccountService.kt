package com.chatpg.security.auth

import com.chatpg.domain.account.Account
import com.chatpg.dto.account.AccountDto
import com.chatpg.dto.auth.OpenAccountCreationDto
import com.chatpg.dto.auth.UuidDto
import com.chatpg.exception.auth.AccountActivationException
import com.chatpg.exception.http.ConflictException
import com.chatpg.exception.requesthandle.ExpiredRequestHandleException
import com.chatpg.infra.email.EmailService
import com.chatpg.infra.email.MimeHelper
import com.chatpg.infra.uri.FrontendUriHelper
import com.chatpg.mapper.AccountMapper
import com.chatpg.security.helper.NullSafePasswordEncoder
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.security.requesthandle.RequestHandleSubject
import com.chatpg.service.account.AccountService
import com.chatpg.service.account.AccountStatus
import com.chatpg.validator.account.PasswordValidator
import com.chatpg.validator.account.UsernameValidator
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class AuthCreateAccountService(
    private val passwordEncoder: NullSafePasswordEncoder,
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val requestHandleService: RequestHandleService,

    private val usernameValidator: UsernameValidator,
    private val passwordValidator: PasswordValidator,

    private val mimeHelper: MimeHelper,
    private val frontendUriHelper: FrontendUriHelper,

    private val accountMapper: AccountMapper,

    @param:Value($$"${security.request-handle.activate-account-expires-in}")
    private val activateAccountExpiresIn: Duration,

    @param:Value($$"${frontend.paths.activate-account}")
    private val activateAccountUrl: String,
) {

    companion object {
        private val subject = RequestHandleSubject.ACTIVATE_ACCOUNT
    }

    @Transactional
    fun openAccountCreation(dto: OpenAccountCreationDto): AccountDto {
        usernameValidator.validate(t = dto.username)
        passwordValidator.validate(t = dto.password)

        val encodedPassword = passwordEncoder.encode(rawPassword = dto.password)

        val unpersistedAccount = Account(dto.username, dto.email, encodedPassword)
        val account = accountService.insertAccount(unpersistedAccount)

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
        val account: Account =
            try {
                requestHandleService.getAccountAndDiscardCheckedHandle(
                    dto.value,
                    subject,
                    expirationTime = activateAccountExpiresIn
                )
            } catch (ex: ExpiredRequestHandleException) {
                accountService.deleteById(ex.accountId)
                throw AccountActivationException("Request expired. Request will be deleted. ")
            }

        isAccountStatusDisabledElseThrow(account)

        accountService.updateStatus(account, status = AccountStatus.ENABLED)
        return accountMapper.toDto(account)
    }

    fun isAccountStatusDisabledElseThrow(account: Account) {
        when (account.status) {
            AccountStatus.DISABLED -> {}
            AccountStatus.ENABLED  -> throw ConflictException("Account already activated")
            AccountStatus.DELETED  -> throw ConflictException("This account is deleted")
        }
    }

}