package com.chatpg.infra.email

import com.chatpg.amqp.ChatpgAmqpConfiguration
import com.chatpg.dto.microservices.email.EmailDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val rabbitTemplate: RabbitTemplate
) {

    private companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Sends MIME e-mail to [to] with subject [subject].
     * Its content is taken from the template found with [templateName].
     * It substitutes the template's variables using the [String] to [String] mapping given before using as content.
     *
     * @param subject Email subject.
     * @param to Email recipient.
     * @param templateName Name of the template that will be used after applying variables [variables].
     */
    fun sendMimeEmailWithTemplate(
        subject: String,
        to: String,
        templateName: String,
        vararg variables: Pair<String, String>
    ) {
        val dto = EmailDto(subject, to, templateName, variables.toMap())

        rabbitTemplate.convertAndSend(
            ChatpgAmqpConfiguration.EMAIL_SEND_EX,
            "",
            dto
        )

        log.info { "Email sent to queue." }
    }

}
