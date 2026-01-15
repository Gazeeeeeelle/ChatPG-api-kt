package com.yourRPG.chatPG.security.filters

import com.auth0.jwt.interfaces.Claim
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.service.account.AccountService
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Spy
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.stream.Stream

class TokenFilterTest: FilterTest() {

    @Spy
    @InjectMocks
    lateinit var tokenFilter: TokenFilter

    @Mock
    lateinit var tokenService: TokenService

    @Mock
    lateinit var accountService: AccountService

    @Mock
    lateinit var account: Account

    @Mock
    lateinit var claim: Claim

    @Mock
    lateinit var securityContext: SecurityContext

    @Mock
    lateinit var mockedSch: MockedStatic<SecurityContextHolder>

    @Test
    fun valid() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/somethingElse")

        given(request.getHeader("Authorization"))
            .willReturn("Bearer valid-token123")

        given(tokenService.getClaim("valid-token123", "id"))
            .willReturn(claim)

        given(accountService.getById(0L))
            .willReturn(account)

        mockedSch
            .`when`<SecurityContext> { SecurityContextHolder.getContext() }
            .thenReturn(securityContext)

        //ACT
        tokenFilter.doFilter(request, response, filterChain)

        //ASSERT
        mockedSch.verify { SecurityContextHolder.getContext() }
    }

    @TestFactory
    fun `valid because such paths are not filtered`(): Stream<DynamicTest> =
        Stream.of(
            "/login", "/login/exists"
        ).map { path ->
            DynamicTest.dynamicTest("path: $path") {
                //ARRANGE
                given(request.servletPath)
                    .willReturn(path)

                //ACT
                tokenFilter.doFilter(request, response, filterChain)

                //ASSERT
                Mockito.verify(request, times(0))
                    .getHeader("Authorization")
            }
        }

    @Test
    fun `invalid because Authorization header is lacking`() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/somethingElse")

        given(request.getHeader("Authorization"))
            .willReturn(null)

        //ACT
        tokenFilter.doFilter(request, response, filterChain)

        //ASSERT
        Mockito.verify(response)
            .sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString())
    }

    @Test
    fun `invalid because a token was given with a principal (Long id value) that did not identify an existing account`() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/somethingElse")

        given(request.getHeader("Authorization"))
            .willReturn("Bearer valid-token123")

        given(tokenService.getClaim("valid-token123", "id"))
            .willReturn(claim)

        given(accountService.getById(0L))
            .willThrow(AccountNotFoundException::class.java)

        //ACT
        tokenFilter.doFilter(request, response, filterChain)

        //ASSERT
        Mockito.verify(response)
            .sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), any())
    }

}