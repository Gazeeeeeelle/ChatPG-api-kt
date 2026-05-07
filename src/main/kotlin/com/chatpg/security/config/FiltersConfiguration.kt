package com.chatpg.security.config

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.security.filters.AccessToChatFilter
import com.chatpg.security.filters.TokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class FiltersConfiguration(
    private val tokenFilter: TokenFilter,
    private val accessToChatFilter: AccessToChatFilter,

    private val swaggerDocumentationSecurityConfigurer: SwaggerDocumentationSecurityConfigurer,

    private val corsConfigurationSource: CorsConfigurationSource,
) {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain = httpSecurity.run {
        cors { it.configurationSource(corsConfigurationSource) }
        csrf { it.disable() }
        sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        authorizeHttpRequests {
            it.requestMatchers("${ApplicationEndpoints.AuthSecure.BASE}/**").authenticated()
            it.requestMatchers("${ApplicationEndpoints.Auth.BASE}/**").permitAll()

            swaggerDocumentationSecurityConfigurer.configure(it)

            it.anyRequest().authenticated()
        }
        addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        addFilterAfter(accessToChatFilter, UsernamePasswordAuthenticationFilter::class.java)
        anonymous { it.disable() }
        build()
    }

}