package com.chatpg.exception.poll

import com.chatpg.exception.http.sc4xx.ConflictException

class AlreadyVotedInPollException(message: String): ConflictException(message)