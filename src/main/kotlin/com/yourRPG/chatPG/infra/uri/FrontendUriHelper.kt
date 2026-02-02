package com.yourRPG.chatPG.infra.uri

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FrontendUriHelper(
    @param:Value("\${server.frontend.address}")
    private val frontendUri: String,
): UriHelper {

    override fun getUriString(): String = frontendUri

}