package com.yourRPG.chatPG.controller.auth.external

import com.yourRPG.chatPG.config.ApplicationEndpoints
import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.auth.external.GoogleLoginService
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApplicationEndpoints.ExternalLogin.GOOGLE)
class GoogleLoginController(
    log: KLogger = KotlinLogging.logger {},
    googleLoginService: GoogleLoginService,
    frontendUriHelper: FrontendUriHelper
): ExternalLoginController(log, googleLoginService, frontendUriHelper)
