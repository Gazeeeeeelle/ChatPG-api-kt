package com.yourRPG.chatPG.security.auth.external

interface ExternalLoginService {

    fun getCodeUrl(): String

    fun loginWithCode(code: String): String

}