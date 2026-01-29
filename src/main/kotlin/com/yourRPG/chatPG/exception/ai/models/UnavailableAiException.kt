package com.yourRPG.chatPG.exception.ai.models

import com.yourRPG.chatPG.exception.http.ServiceUnavailableException

class UnavailableAiException(message: String): ServiceUnavailableException(message)