package com.yourRPG.chatPG.exception.handler

import com.yourRPG.chatPG.exception.auth.A2FRequiredException
import com.yourRPG.chatPG.exception.http.ConflictException
import com.yourRPG.chatPG.exception.http.ForbiddenException
import com.yourRPG.chatPG.exception.http.HttpException
import com.yourRPG.chatPG.exception.http.NotFoundException
import com.yourRPG.chatPG.exception.http.ServiceUnavailableException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(A2FRequiredException::class)
    fun a2FRequiredException(ex: A2FRequiredException): ResponseEntity<Unit> {
        val headers = HttpHeaders().apply {
            location = ex.uri
        }

        return ResponseEntity.status(302)
            .headers(headers)
            .build()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<String> =
        ResponseEntity.status(400).body(ex.fieldError?.defaultMessage)

    @ExceptionHandler(HttpException::class)
    fun httpException(ex: HttpException): ResponseEntity<String> =
        ResponseEntity.status(ex.status).body(ex.message)

}