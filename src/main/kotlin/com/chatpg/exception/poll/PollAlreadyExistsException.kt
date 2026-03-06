package com.chatpg.exception.poll

import com.chatpg.exception.http.ConflictException

class PollAlreadyExistsException(message: String): ConflictException(message)
