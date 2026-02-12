package com.yourRPG.chatPG.infra.external.auth

interface IAuthApiService {

    fun getCodeUrl(): String

    fun getEmail(code: String): String

}