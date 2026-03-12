package com.chatpg.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class SwaggerDocSecurityConfigurer(
    @param:Value($$"${springdoc.api-docs.enabled}")
    val apiDocsEnabled: Boolean,

    @param:Value($$"${springdoc.api-docs.path}")
    val apiDocsPath: String,

    @param:Value($$"${springdoc.swagger-ui.path}")
    val docHtmlPath: String,
) {

    fun isSwaggerDocPath(path: String) =
        path.startsWith(apiDocsPath)

    fun configure(
        customizer: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
    ) = customizer.run {
        if (apiDocsEnabled) requestMatchers("$apiDocsPath/**", "$docHtmlPath/**").permitAll()
    }

}