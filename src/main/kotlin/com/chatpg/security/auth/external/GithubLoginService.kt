package com.chatpg.security.auth.external

import com.chatpg.infra.external.auth.github.GithubAuthApiService
import com.chatpg.security.requesthandle.RequestHandleService
import com.chatpg.service.account.AccountService
import org.springframework.stereotype.Service

@Service
class GithubLoginService(
    githubAuthApiService: GithubAuthApiService,
    accountService: AccountService,
    requestHandleService: RequestHandleService,
): ExternalLoginService(githubAuthApiService, accountService, requestHandleService)
