package com.chatpg.dto.microservices.email

data class EmailDto (
    val subject: String,
    val to: String,
    val template: String,
    val variables: Map<String, String>
)