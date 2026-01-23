package com.yourRPG.chatPG.helper.email

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class MimeHelper(
    private val templateEngine: TemplateEngine
) {

    @Value("\${server.frontend.address}")
    private lateinit var frontEndAddress: String

    fun getTemplate(template: String, vararg variables: Pair<String, String>): String {
        val context = Context().apply {
            for ((name, value) in variables) {
                setVariable(name, value)
            }
        }

        return templateEngine.process(template, context)
    }

}