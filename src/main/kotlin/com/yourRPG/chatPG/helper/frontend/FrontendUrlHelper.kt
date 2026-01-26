package com.yourRPG.chatPG.helper.frontend

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FrontendUrlHelper(
    @param:Value("\${server.frontend.address}")
    private val frontendUrl: String,
) {

    fun append(value: String): String {
        return frontendUrl + value
    }

    fun getUrl(): String = frontendUrl

}