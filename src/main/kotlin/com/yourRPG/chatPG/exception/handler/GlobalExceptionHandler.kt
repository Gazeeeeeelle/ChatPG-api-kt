package com.yourRPG.chatPG.exception.handler

import com.yourRPG.chatPG.exception.http.ConflictException
import com.yourRPG.chatPG.exception.http.ForbiddenException
import com.yourRPG.chatPG.exception.http.NotFoundException
import com.yourRPG.chatPG.exception.http.ServiceUnavailableException
import com.yourRPG.chatPG.exception.http.UnauthorizedException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity.status(400).body(ex.fieldError?.defaultMessage)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgument(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.status(400).body(ex.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorized(ex: UnauthorizedException): ResponseEntity<String> {
        return ResponseEntity.status(401).body(ex.message)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun forbidden(ex: ForbiddenException): ResponseEntity<String> {
        return ResponseEntity.status(403).body(ex.message)
    }

    @ExceptionHandler(NotFoundException::class)
    fun notFound(ex: NotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(404).body(ex.message)
    }

    @ExceptionHandler(ConflictException::class)
    fun conflict(ex: ConflictException): ResponseEntity<String> {
        return ResponseEntity.status(409).body(ex.message)
    }

    @ExceptionHandler(ServiceUnavailableException::class)
    fun serviceUnavailable(ex: ServiceUnavailableException): ResponseEntity<String> {
        return ResponseEntity.status(503).body(ex.message)
    }

}