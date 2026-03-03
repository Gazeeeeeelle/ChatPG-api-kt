package com.chatpg.infra.email

import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class MimeHelper(
    private val templateEngine: TemplateEngine,
) {

    fun getTemplate(template: String, vararg variables: Pair<String, String>): String {
        val context = Context().apply {
            for ((name, value) in variables) {
                setVariable(name, value)
            }
        }

        return templateEngine.process(template, context)
    }

}