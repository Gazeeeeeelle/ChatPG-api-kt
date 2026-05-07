package com.chatpg.infra.uri

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Component
class GatewayUriHelper(
    @param:Value($$"${security.protocol}")
    private val protocol: String,

    @param:Value($$"${gateway.address}")
    private val address: String,

    @param:Value($$"${gateway.port}")
    private val port: String,

    val socketAddress: String = "$address:$port"
): UriHelper {

    override fun getUriString(): String = "$protocol://$socketAddress"

}