package com.yourRPG.chatPG.infra.uri

import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Component
class BackendUriHelper: UriHelper {

    override fun getUriString(): String = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()

}