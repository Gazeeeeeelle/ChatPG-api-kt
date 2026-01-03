package com.yourRPG.chatPG.exception.ai.models

import com.yourRPG.chatPG.exception.ServiceUnavailableException

class UnavailableAiException(message: String): ServiceUnavailableException(message)