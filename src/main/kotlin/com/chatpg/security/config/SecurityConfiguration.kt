package com.chatpg.security.config

import com.chatpg.config.ApplicationEndpoints
import com.chatpg.infra.uri.FrontendUriHelper
import com.chatpg.security.filters.AccessToChatFilter
import com.chatpg.security.filters.TokenFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val tokenFilter: TokenFilter,
    private val accessToChatFilter: AccessToChatFilter,
    private val frontendUriHelper: FrontendUriHelper,

    private val swaggerDocSecurityConfigurer: SwaggerDocSecurityConfigurer,

    @param:Value($$"${security.bcrypt-password-strength}")
    private val bCryptPasswordStrength: Int,
) {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain = httpSecurity.run {
        cors { it.configurationSource(corsConfigurationSource()) }
        csrf { it.disable() }
        sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        authorizeHttpRequests {
            it.requestMatchers("${ApplicationEndpoints.AuthSecure.BASE}/**").authenticated()
            it.requestMatchers("${ApplicationEndpoints.Auth.BASE}/**").permitAll()

            swaggerDocSecurityConfigurer.configure(it)

            it.anyRequest().authenticated()
        }
        addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        addFilterAfter(accessToChatFilter, UsernamePasswordAuthenticationFilter::class.java)
        build()
    }

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

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager =
        configuration.authenticationManager

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(bCryptPasswordStrength)

}
