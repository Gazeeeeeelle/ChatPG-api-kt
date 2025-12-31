package com.yourRPG.chatPG.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfiguration: WebMvcConfigurer {

    @Value("\${server.address}")
    private lateinit var address: String;

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://$address:5500",
                "http://26.17.132.72:8081",
                "http://127.0.0.1:5500",
                "http://26.17.132.72:5500")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD", "TRACE", "CONNECT")
    }

}
