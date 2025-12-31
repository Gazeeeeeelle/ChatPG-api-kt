package com.yourRPG.chatPG.exception.message

import com.yourRPG.chatPG.exception.NotFoundException

class MessageNotFoundException(message: String): NotFoundException(message) {
}