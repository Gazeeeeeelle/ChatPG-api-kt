package com.yourRPG.chatPG.helper.http

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class CookieService {

    fun appendCookie(response: HttpServletResponse, name: String, value: String) {
        response.addCookie(Cookie(name, value).apply {
            secure = false
            isHttpOnly = true
        })
    }

}