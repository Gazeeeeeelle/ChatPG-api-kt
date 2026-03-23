package com.chatpg.security.config

import com.chatpg.infra.uri.FrontendUriHelper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfiguration(
    private val frontendUriHelper: FrontendUriHelper,
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource = UrlBasedCorsConfigurationSource().apply {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(
                frontendUriHelper.getUriString(),
            )
            allowCredentials = true
            allowedMethods =
                listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD", "TRACE", "CONNECT")
            allowedHeaders =
                listOf("Authorization", "Content-Type", "credentials")
        }

        registerCorsConfiguration("/**", configuration)
    }


}