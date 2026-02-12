package com.yourRPG.chatPG.exception.handler

import com.yourRPG.chatPG.dto.error.RedirectDto
import com.yourRPG.chatPG.exception.auth.A2fRequiredException
import com.yourRPG.chatPG.exception.http.HttpException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(A2fRequiredException::class)
    fun a2fRequiredException(ex: A2fRequiredException): ResponseEntity<RedirectDto> {
        val dto = RedirectDto("Further authentication needed.", ex.url)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto)
    }

    @ExceptionHandler(HttpException::class)
    fun httpException(ex: HttpException): ResponseEntity<String> =
        ResponseEntity.status(ex.status).body(ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<String> =
        ResponseEntity.badRequest().body(ex.fieldError?.defaultMessage)

    @ExceptionHandler(MissingRequestCookieException::class)
    fun missingRequestCookieException(ex: MissingRequestCookieException): ResponseEntity<String> =
        ResponseEntity.badRequest().body("${ex.cookieName} is missing.")

}