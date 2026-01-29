package com.yourRPG.chatPG.security.config

import com.yourRPG.chatPG.helper.uri.FrontendUriHelper
import com.yourRPG.chatPG.security.filters.AccessToChatFilter
import com.yourRPG.chatPG.security.filters.TokenFilter
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

    @param:Value("\${server.protocol}")
    private val protocol: String,

    @param:Value("\${server.address}")
    private val address: String
) {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain = with(httpSecurity) {
        cors { it.configurationSource(corsConfigurationSource()) }
        csrf { it.disable() }
        sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        authorizeHttpRequests {
            it.requestMatchers("/auth/logout").authenticated()
            it.requestMatchers("/auth/**").permitAll()

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
                "$protocol$address:5500",
                frontendUriHelper.getUriString()
            )
            allowCredentials = true
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
