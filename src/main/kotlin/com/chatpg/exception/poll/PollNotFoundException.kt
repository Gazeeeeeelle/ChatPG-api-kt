package com.chatpg.exception.poll

import com.chatpg.exception.http.sc4xx.NotFoundException

class PollNotFoundException(message: String) : NotFoundException(message)