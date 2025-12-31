package com.yourRPG.chatPG.service.http

import org.springframework.stereotype.Service
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class HttpService {

    private val client: HttpClient = HttpClient.newHttpClient()

    /**
     * TODO
     */
    fun <T> send(request: HttpRequest, responseBodyHandler: HttpResponse.BodyHandler<T>): HttpResponse<T> {
        return client.send(request, responseBodyHandler)
    }

}