package com.yourRPG.chatPG.exception.poll

import com.yourRPG.chatPG.exception.http.ConflictException

class PollAlreadyExistsException(message: String): ConflictException(message) {
}