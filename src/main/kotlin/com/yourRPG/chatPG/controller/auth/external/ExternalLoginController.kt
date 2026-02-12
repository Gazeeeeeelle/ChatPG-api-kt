package com.yourRPG.chatPG.controller.auth.external

import com.yourRPG.chatPG.infra.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.auth.external.ExternalLoginService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URI

open class ExternalLoginController(
    private val externalLoginService: ExternalLoginService,
    private val frontendUriHelper: FrontendUriHelper,
) {

    @GetMapping
    fun codeUrl(): ResponseEntity<Unit> {
        val url = externalLoginService.getCodeUrl()

        val headers = HttpHeaders().apply { location = URI.create(url) }
        return ResponseEntity.status(HttpStatus.FOUND)
            .headers(headers)
            .build()
    }

    @GetMapping("/authorized")
    fun authorized(
        @RequestParam code: String
    ): ResponseEntity<Unit> {
        val requestHandle = externalLoginService.loginWithCode(code)

        val headers = HttpHeaders().apply {
            location = frontendUriHelper.run { appendUri("$authorizedPath?uuid=$requestHandle") }
        }

        return ResponseEntity.status(HttpStatus.FOUND)
            .headers(headers)
            .build()
    }


}