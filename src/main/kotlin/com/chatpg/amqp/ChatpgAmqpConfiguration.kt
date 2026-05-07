package com.chatpg.amqp

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatpgAmqpConfiguration(
    private val rabbitReturnsCallback: RabbitReturnsCallback,

    @param:Value($$"${spring.amqp.host}")
    private val rabbitmqHost: String
) {

    companion object {
        const val EMAIL_SEND_Q = "email.send.queue"
        const val EMAIL_SEND_EX = "email.send.exchange"
    }

    @Bean
    fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin =
        RabbitAdmin(connectionFactory)

    @Bean
    fun initializeAdmin(rabbitAdmin: RabbitAdmin): ApplicationListener<ApplicationReadyEvent> =
        { _ -> rabbitAdmin.initialize() }

    @Bean
    fun messageConverter(): JacksonJsonMessageConverter =
        JacksonJsonMessageConverter()

    @Bean
    fun connectionFactory(): CachingConnectionFactory =
        CachingConnectionFactory(rabbitmqHost).apply {
            isPublisherReturns = true
            setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED)
        }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: JacksonJsonMessageConverter): RabbitTemplate =
        RabbitTemplate(connectionFactory).apply {
            this.messageConverter = messageConverter

            setReturnsCallback(rabbitReturnsCallback)
            setMandatory(true)
        }

}