package com.chatpg.exception.ai.models

import com.chatpg.exception.http.sc5xx.ServiceUnavailableException

class UnavailableAiException(message: String): ServiceUnavailableException(message)