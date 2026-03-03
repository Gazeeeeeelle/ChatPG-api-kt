package com.chatpg.controller.auth.external

import com.chatpg.infra.uri.FrontendUriHelper
import com.chatpg.security.auth.external.ExternalLoginService
import io.github.oshai.kotlinlogging.KLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

open class ExternalLoginController(
    private val log: KLogger,
    private val externalLoginService: ExternalLoginService,
    private val frontendUriHelper: FrontendUriHelper,
) {
    @GetMapping
    fun codeUrl(): ResponseEntity<Unit> {
        log.info { "External login requested" }
        val url = externalLoginService.getCodeUrl()

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, url)
            .build()
    }

    @GetMapping("/authorized")
    fun authorized(
        @RequestParam code: String,
    ): ResponseEntity<Unit> {
        log.info { "External login authorized" }
        val requestHandle = externalLoginService.loginWithCode(code)

        val location = frontendUriHelper.run { appendString("$authorizedPath?uuid=$requestHandle") }
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, location)
            .build()
    }
}

