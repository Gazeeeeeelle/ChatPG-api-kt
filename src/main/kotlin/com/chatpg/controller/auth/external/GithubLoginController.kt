package com.chatpg.controller.auth.external

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.infra.uri.FrontendUriHelper
import com.chatpg.security.auth.external.GithubLoginService
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApplicationEndpoints.ExternalLogin.GITHUB)
class GithubLoginController(
    log: KLogger = KotlinLogging.logger {},
    githubLoginService: GithubLoginService,
    frontendUriHelper: FrontendUriHelper,
) : ExternalLoginController(log, githubLoginService, frontendUriHelper)
