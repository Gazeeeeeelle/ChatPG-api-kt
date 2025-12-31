package com.yourRPG.chatPG

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatPgApplication

fun main(args: Array<String>) {
	runApplication<ChatPgApplication>(*args)
}
