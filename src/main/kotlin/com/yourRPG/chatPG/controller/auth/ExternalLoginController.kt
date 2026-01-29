package com.yourRPG.chatPG.controller.auth

import com.yourRPG.chatPG.helper.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.auth.external.LoginGithubService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/auth/login/with")
class ExternalLoginController(
    private val loginGithubService: LoginGithubService,
    private val frontendUriHelper: FrontendUriHelper,
) {

    @GetMapping("/github")
    fun github(): ResponseEntity<Void> {
        val url = loginGithubService.getCodeUrl()

        val headers = HttpHeaders().apply { location = URI.create(url) }
        return ResponseEntity.status(HttpStatus.FOUND)
            .headers(headers).build()
    }

    @GetMapping("/github/authorized")
    fun authorized(
        @RequestParam code: String
    ): ResponseEntity<Void> {
        val accessToken = loginGithubService.loginWithCode(code)

        val headers = HttpHeaders().apply {
            location = URI.create(frontendUriHelper.append("/login/authorized?token=$accessToken"))
        }

        return ResponseEntity.status(HttpStatus.FOUND)
            .headers(headers).build()
    }

}