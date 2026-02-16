package com.yourRPG.chatPG.security.auth.external

import com.yourRPG.chatPG.infra.external.auth.google.GoogleAuthApiService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.stereotype.Service

@Service
class GoogleLoginService(
    googleAuthApiService: GoogleAuthApiService,
    accountService: AccountService,
    requestHandleService: RequestHandleService,
): ExternalLoginService(googleAuthApiService, accountService, requestHandleService)
