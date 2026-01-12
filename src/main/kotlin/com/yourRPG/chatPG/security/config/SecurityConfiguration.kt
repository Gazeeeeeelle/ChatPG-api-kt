package com.yourRPG.chatPG.security.config

import com.yourRPG.chatPG.security.filters.AccessToChatFilter
import com.yourRPG.chatPG.security.filters.TokenFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
    private val accessToChatFilter: AccessToChatFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = with(http) {
        cors { it.configurationSource(corsConfigurationSource()) }
        csrf { it.disable() }
        sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        authorizeHttpRequests {
            it.requestMatchers(HttpMethod.POST, "/login/**").permitAll()
                .anyRequest().authenticated()
        }
        addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        addFilterAfter(accessToChatFilter, UsernamePasswordAuthenticationFilter::class.java)
        build()
    }

    @Value("\${server.address}")
    private lateinit var address: String

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource = UrlBasedCorsConfigurationSource().apply {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://$address:5500",
                "http://26.17.132.72:8081",
                "http://127.0.0.1:5500",
                "http://26.17.132.72:5500"
            )
            allowedMethods =
                listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD", "TRACE", "CONNECT")
            allowedHeaders =
                listOf("Authorization", "Content-Type")
        }

        registerCorsConfiguration("/**", configuration)
    }

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager =
        configuration.authenticationManager

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}
