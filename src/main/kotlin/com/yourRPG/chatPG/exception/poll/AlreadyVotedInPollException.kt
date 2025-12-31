package com.yourRPG.chatPG.exception.poll

import com.yourRPG.chatPG.exception.ConflictException

class AlreadyVotedInPollException(message: String): ConflictException(message)