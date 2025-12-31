package com.yourRPG.chatPG.exception

import com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
import com.yourRPG.chatPG.exception.poll.PollAlreadyExistsException
import org.apache.coyote.BadRequestException
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun notFoundException(ex: NotFoundException): ResponseEntity<String?> {
        return ResponseEntity.status(404).body(ex.message)
    }

    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(ex: BadRequestException): ResponseEntity<String?> {
        return ResponseEntity.status(400).body(ex.message)
    }

    @ExceptionHandler(UnavailableAiException::class)
    fun unavailableAIException(ex: UnavailableAiException): ResponseEntity<String?> {
        return ResponseEntity.status(503).body(ex.message)
    }

    @ExceptionHandler(PollAlreadyExistsException::class)
    fun pollAlreadyExistsException(ex: PollAlreadyExistsException): ResponseEntity<String?> {
        return ResponseEntity.status(409).body(ex.message)
    }

}