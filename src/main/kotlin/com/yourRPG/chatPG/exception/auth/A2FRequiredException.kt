package com.yourRPG.chatPG.exception.auth

import java.net.URI

class A2FRequiredException(val uri: URI): RuntimeException()