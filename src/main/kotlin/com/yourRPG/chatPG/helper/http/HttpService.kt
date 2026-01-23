package com.yourRPG.chatPG.helper.http

import org.springframework.stereotype.Service
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class HttpService {

    private val client: HttpClient = HttpClient.newHttpClient()

    /**
     * Wraps the [HttpClient]'s [send]
     *
     * @param request
     * @param responseBodyHandler
     * @return [java.net.http.HttpResponse]
     * @throws java.io.IOException
     * @throws InterruptedException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see HttpClient.send
     */
    fun <T> send(request: HttpRequest, responseBodyHandler: HttpResponse.BodyHandler<T>): HttpResponse<T> =
        client.send(request, responseBodyHandler)

}