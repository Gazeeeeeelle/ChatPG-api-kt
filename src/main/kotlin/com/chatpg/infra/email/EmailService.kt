package com.chatpg.infra.email

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val mimeHelper: MimeHelper,
) {

    /**
     * Sends e-mail to [to] with subject [subject] and contents [text].
     *
     * @param subject Email subject
     * @param to Email recipient
     * @param text Email content
     */
    fun sendEmail(subject: String, to: String, text: String) {
        val message = SimpleMailMessage().apply {
            setTo(to)
            this.subject = subject
            this.text = text
        }

        mailSender.send(message)
    }

    /**
     * Sends MIME e-mail to [to] with subject [subject] and with HTML contents [html].
     *
     * @param subject Email subject
     * @param to Email recipient
     * @param html Email content
     */
    fun sendMimeEmail(subject: String, to: String, html: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(to)
            setSubject(subject)
            setText(html, true)
        }

        mailSender.send(message)
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
        val html = mimeHelper.getTemplate(templateName, *variables)

        val message: MimeMessage = mailSender.createMimeMessage()

        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(to)
            setSubject(subject)
            setText(html, true)
        }

        mailSender.send(message)
    }

}