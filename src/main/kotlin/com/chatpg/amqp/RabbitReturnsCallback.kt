package com.chatpg.amqp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.core.ReturnedMessage
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class RabbitReturnsCallback: RabbitTemplate.ReturnsCallback {

    companion object {
        val log = KotlinLogging.logger {}
    }

    override fun returnedMessage(returned: ReturnedMessage) {
        log.error {
            "UNROUTABLE MESSAGE RETURNED - " +
                    "Exchange: ${returned.exchange}, " +
                    "Routing Key: ${returned.routingKey}, " +
                    "Reason: ${returned.replyText}"
        }
    }

}