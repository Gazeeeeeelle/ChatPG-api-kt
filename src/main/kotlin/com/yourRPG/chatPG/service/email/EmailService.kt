package com.yourRPG.chatPG.service.email

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    val mailSender: JavaMailSender,
) {

    fun sendEmail(subject: String, to: String, text: String) {
        val message = SimpleMailMessage().apply {
            setTo(to)
            this.subject = subject
            this.text = text
        }

        mailSender.send(message)
    }

    fun sendMimeEmail(subject: String, to: String, html: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setTo(to)
            setSubject(subject)
            setText(html, true)
        }

        mailSender.send(message)
    }

}