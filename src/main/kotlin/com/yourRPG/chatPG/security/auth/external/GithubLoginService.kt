package com.yourRPG.chatPG.security.auth.external

import com.yourRPG.chatPG.infra.external.auth.github.GithubAuthApiService
import com.yourRPG.chatPG.security.requesthandle.RequestHandleService
import com.yourRPG.chatPG.security.token.TokenManagerService
import com.yourRPG.chatPG.service.account.AccountService
import org.springframework.stereotype.Service

@Service
class GithubLoginService(
    githubAuthApiService: GithubAuthApiService,
    accountService: AccountService,
    requestHandleService: RequestHandleService,
): ExternalLoginService(githubAuthApiService, accountService, requestHandleService)
