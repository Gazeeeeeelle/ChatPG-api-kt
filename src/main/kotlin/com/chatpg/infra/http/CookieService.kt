package com.chatpg.infra.http

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service

@Service
class CookieService(
    @param:Value($$"${security.http.cookie.secure:true}")
    private val isSecure: Boolean,
) {

    fun appendCookie(name: String, value: String): ResponseCookie =
        ResponseCookie.from(name, value).apply {
            httpOnly(true)
            secure(isSecure)
            path("/api")
        }.build()

    fun refreshToken(refreshToken: String) =
        appendCookie("refresh_token", refreshToken)

}