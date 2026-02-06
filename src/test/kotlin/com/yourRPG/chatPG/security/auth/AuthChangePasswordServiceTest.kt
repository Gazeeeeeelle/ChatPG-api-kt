package com.yourRPG.chatPG.security.auth

import com.yourRPG.chatPG.infra.email.EmailService
import com.yourRPG.chatPG.infra.email.MimeHelper
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.service.account.AccountService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class AuthChangePasswordServiceTest {

    private lateinit var service: AuthChangePasswordService

    @Mock private lateinit var passwordEncoder: PasswordEncoder
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var emailService: EmailService
    @Mock private lateinit var requestHandleService: RequestHandleService
    @Mock private lateinit var frontendUriHelper: FrontendUriHelper
    @Mock private lateinit var mimeHelper: MimeHelper

    private val changePasswordExpiresIn = Duration.ofMinutes(10L)

    @BeforeEach
    fun setUp() {
        service = AuthChangePasswordService(
            passwordEncoder,
            accountService,
            emailService,
            requestHandleService,
            frontendUriHelper,
            mimeHelper,
            changePasswordExpiresIn
        )
    }

    //TODO

}