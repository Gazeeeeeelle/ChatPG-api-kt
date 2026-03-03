package com.chatpg.infra.uri

import java.net.URI

interface UriHelper {

    fun appendString(path: String): String = getUriString() + path

    fun appendUri(path: String): URI = URI.create(appendString(path))

    fun getUriString(): String

}