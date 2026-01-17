package com.yourRPG.chatPG.security.token

import com.auth0.jwt.JWT
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.service.account.AccountService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.exception.security.InvalidTokenException
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UserDetails
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
class TokenServiceTest {

    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var accountService: AccountService

    @Mock
    private lateinit var userDetails: UserDetails

    @Mock
    private lateinit var account: Account

    private val secret = "secret"

    private val issuer = "API ChatPG"

    private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

    @BeforeEach
    fun setup() {
        tokenService = TokenService(accountService, secret, clock)
    }

    @Test
    fun `generateToken - success`() {
        //ARRANGE
        val name = "Daniel"

        given(userDetails.username)
            .willReturn(name)

        given(accountService.getByName(name))
            .willReturn(account)

        //ACT
        val token = tokenService.generateToken(userDetails)

        //ASSERT
        verify(accountService)
            .getByName(name)

        assertDoesNotThrow { getClaim(token, "id") }

    }

    @Test
    fun `generateToken - failure due to account absence`() {
        //ARRANGE
        val name = "Daniel"

        given(userDetails.username)
            .willReturn(name)

        val message = "Account not found"
        given(accountService.getByName(name))
            .willThrow(AccountNotFoundException(message))

        //ACT + ASSERT
        val ex = assertThrows<AccountNotFoundException> {
            tokenService.generateToken(userDetails)
        }

        assertEquals(message, ex.message)
        verify(accountService)
            .getByName(name)
    }

    @Test
    fun `generateToken - failure due to id abnormal absence`() {
        //ARRANGE
        val name = "Daniel"

        given(userDetails.username)
            .willReturn(name)

        given(accountService.getByName(name))
            .willReturn(account)

        given(account.id)
            .willReturn(null)

        //ACT + ASSERT
        val ex = assertThrows<AccountNotFoundException> {
            tokenService.generateToken(userDetails)
        }

        assertEquals("ID not found", ex.message)

    }

    fun getClaim(token: String, claim: String): Claim =
        runCatching {
            JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build()
                .verify(token)
                .claims[claim]
                ?: throw InvalidTokenException("Invalid claim")
        }.getOrElse { ex ->
            throw InvalidTokenException(ex.message ?: "Token invalid")
        }


}