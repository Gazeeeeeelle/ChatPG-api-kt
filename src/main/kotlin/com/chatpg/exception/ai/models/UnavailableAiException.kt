package com.chatpg.exception.ai.models

import com.chatpg.exception.http.ServiceUnavailableException

class UnavailableAiException(message: String): ServiceUnavailableException(message)