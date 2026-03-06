package com.chatpg.exception.poll

import com.chatpg.exception.http.ConflictException

class AlreadyVotedInPollException(message: String): ConflictException(message)