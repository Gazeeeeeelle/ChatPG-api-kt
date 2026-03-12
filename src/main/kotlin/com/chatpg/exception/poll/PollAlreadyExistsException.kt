package com.chatpg.exception.poll

import com.chatpg.exception.http.sc4xx.ConflictException

class PollAlreadyExistsException(message: String): ConflictException(message)
