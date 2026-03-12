package com.chatpg.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

/**
 * Responsible for Value Injection of values regarding Swagger Documentation and operations needed with such values.
 */
@Component
class SwaggerDocumentationSecurityConfigurer(
    @param:Value($$"${springdoc.api-docs.enabled}")
    private val isSwaggerDocumentationEnabled: Boolean,

    @param:Value($$"${springdoc.api-docs.path}")
    private val swaggerDocumentationPath: String,

    @param:Value($$"${springdoc.swagger-ui.path}")
    private val swaggerDocumentationHtmlPath: String,
) {

    /**
     * Returns a [Boolean] on whether the path given is included in [swaggerDocumentationPath].
     *
     * @param path Path to be evaluated.
     * @return True If the path given is included in [swaggerDocumentationPath].
     */
    fun isPathSwaggerRelated(path: String): Boolean = path.run {
        startsWith(swaggerDocumentationPath)
                || startsWith(swaggerDocumentationHtmlPath)
    }

    /**
     * Applies HTTP Requests authorization configurations regarding Swagger Documentation if
     *  [isSwaggerDocumentationEnabled].
     *
     * @param customizer Used to apply the configuration regarding Swagger Documentation accessibility.
     */
    fun configure(
        customizer: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
    ): Unit = customizer.run {
        if (isSwaggerDocumentationEnabled)
            requestMatchers(
                "$swaggerDocumentationPath/**",
                "$swaggerDocumentationHtmlPath/**"
            ).permitAll()
    }

}