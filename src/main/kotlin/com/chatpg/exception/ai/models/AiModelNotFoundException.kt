package com.chatpg.exception.ai.models

import com.chatpg.exception.http.sc4xx.NotFoundException

class AiModelNotFoundException(message: String): NotFoundException(message)