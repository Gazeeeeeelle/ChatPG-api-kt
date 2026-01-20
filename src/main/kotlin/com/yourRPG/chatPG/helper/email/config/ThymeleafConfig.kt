package com.yourRPG.chatPG.helper.email.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

@Configuration
class ThymeleafConfig {

    @Bean
    fun templateEngine(): TemplateEngine {
        val resolver = ClassLoaderTemplateResolver().apply {
            prefix = "templates/" // Looks in src/main/resources/templates/
            suffix = ".html"
            templateMode = TemplateMode.HTML
            characterEncoding = "UTF-8"
        }

        return TemplateEngine().apply {
            setTemplateResolver(resolver)
        }
    }

}