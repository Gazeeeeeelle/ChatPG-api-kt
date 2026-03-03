package com.chatpg.infra.uri

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FrontendUriHelper(
    @param:Value($$"${frontend.address}")
    private val frontendUri: String,

    @param:Value($$"${frontend.paths.authorized}")
    val authorizedPath: String,

    @param:Value($$"${frontend.paths.a2f}")
    val a2fPath: String
): UriHelper {

    override fun getUriString(): String = frontendUri

}