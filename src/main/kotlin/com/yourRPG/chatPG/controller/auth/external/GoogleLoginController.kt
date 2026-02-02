package com.yourRPG.chatPG.controller.auth.external

import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.auth.external.GoogleLoginService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/login/with/google")
class GoogleLoginController(
    googleLoginService: GoogleLoginService,
    frontendUriHelper: FrontendUriHelper
): ExternalLoginController(googleLoginService, frontendUriHelper)
