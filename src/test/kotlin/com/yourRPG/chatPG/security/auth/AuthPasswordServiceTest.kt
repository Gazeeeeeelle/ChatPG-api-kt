package com.yourRPG.chatPG.security.auth

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class AuthPasswordServiceTest {

    @InjectMocks
    private lateinit var service: AuthPasswordService

    @Mock private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun encrypt() {
        //ARRANGE
        val rawPassword = "Password-test"
        val encodedPassword = "encoded-Password-test"

        given(passwordEncoder.encode(rawPassword))
            .willReturn(encodedPassword)

        //ACT
        val result = service.encrypt(rawPassword)

        //ASSERT
        assertEquals(encodedPassword, result)
    }

}