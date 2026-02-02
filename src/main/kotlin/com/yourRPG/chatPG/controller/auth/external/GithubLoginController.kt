package com.yourRPG.chatPG.controller.auth.external

import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.auth.external.GithubLoginService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/login/with/github")
class GithubLoginController(
    githubLoginService: GithubLoginService,
    frontendUriHelper: FrontendUriHelper,
): ExternalLoginController(githubLoginService, frontendUriHelper)
