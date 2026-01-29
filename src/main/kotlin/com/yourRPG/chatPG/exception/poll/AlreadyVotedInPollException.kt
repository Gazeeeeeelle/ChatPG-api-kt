package com.yourRPG.chatPG.exception.poll

import com.yourRPG.chatPG.exception.http.ConflictException

class AlreadyVotedInPollException(message: String): ConflictException(message)