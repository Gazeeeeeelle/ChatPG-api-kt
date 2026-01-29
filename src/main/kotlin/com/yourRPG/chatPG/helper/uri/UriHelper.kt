package com.yourRPG.chatPG.helper.uri

interface UriHelper {

    fun append(path: String): String = getUriString() + path

    fun getUriString(): String

}