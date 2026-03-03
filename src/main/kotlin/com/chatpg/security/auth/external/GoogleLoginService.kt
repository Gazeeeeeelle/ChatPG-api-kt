package com.chatpg.security.auth.external

import com.chatpg.infra.external.auth.google.GoogleAuthApiService
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.service.account.AccountService
import org.springframework.stereotype.Service

@Service
class GoogleLoginService(
    googleAuthApiService: GoogleAuthApiService,
    accountService: AccountService,
    requestHandleService: RequestHandleService,
): ExternalLoginService(googleAuthApiService, accountService, requestHandleService)
