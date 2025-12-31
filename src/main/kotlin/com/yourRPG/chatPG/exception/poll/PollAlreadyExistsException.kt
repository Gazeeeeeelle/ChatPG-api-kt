package com.yourRPG.chatPG.exception.poll

import com.yourRPG.chatPG.exception.ConflictException

class PollAlreadyExistsException(message: String): ConflictException(message) {
}