package com.yourRPG.chatPG.service.http

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
     * @return [HttpResponse]
     * @throws java.io.IOException
     * @throws InterruptedException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see HttpClient.send
     */
    fun <T> send(request: HttpRequest, responseBodyHandler: HttpResponse.BodyHandler<T>): HttpResponse<T> {
        return client.send(request, responseBodyHandler)
    }

}