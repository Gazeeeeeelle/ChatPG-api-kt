package com.yourRPG.chatPG.security.filters

import com.auth0.jwt.interfaces.Claim
import com.yourRPG.chatPG.domain.Account
import com.yourRPG.chatPG.exception.account.AccountNotFoundException
import com.yourRPG.chatPG.security.helper.SecurityContextHelper
import com.yourRPG.chatPG.security.token.TokenService
import com.yourRPG.chatPG.service.account.AccountService
import helper.NullSafeMatchers.LONG_TYPE
import helper.NullSafeMatchers.STRING_TYPE
import helper.NullSafeMatchers.any
import helper.NullSafeMatchers.eq
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.GrantedAuthority
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class TokenFilterTest: FilterTest() {

    @InjectMocks
    lateinit var tokenFilter: TokenFilter

    @Mock lateinit var tokenService: TokenService
    @Mock lateinit var accountService: AccountService
    @Mock lateinit var securityContext: SecurityContextHelper

    @Mock
    lateinit var claim: Claim

    @TestFactory
    fun `valid - paths are filtered`(): Stream<DynamicTest> {
        //ARRANGE
        val name     = "username_test"
        val email    = "email@email.com"
        val password = "Password-test"
        val token    = "valid-token123"

        val account  = Account(name, email, password)

        return Stream.of(
            "/auth/logout", "/notAuthRelatedAndCheckedByTokenFilter"
        ).map { path ->
            DynamicTest.dynamicTest("path: $path") {
                //ARRANGE
                given(request.servletPath)
                    .willReturn(path)

                given(tokenService.getAccessToken(request))
                    .willReturn(token)

                given(tokenService.getClaim(token, "id"))
                    .willReturn(claim)

                given(accountService.getById(0L))
                    .willReturn(account)

                //ACT
                tokenFilter.doFilter(request, response, filterChain)

                //ASSERT
                verify(tokenService).apply {
                    getAccessToken(request)
                    getClaim(token, "id")
                }
                verify(accountService).getById(0L)
                verify(securityContext).setPrincipal(0L, account.authorities)

                reset(request, tokenService, accountService, securityContext)
            }
        }
    }

    @TestFactory
    fun `valid - path is not filtered`(): Stream<DynamicTest> =
        Stream.of(
            "/auth/login", "/auth/refreshToken"
        ).map { path ->
            DynamicTest.dynamicTest("path: $path") {
                //ARRANGE
                given(request.servletPath)
                    .willReturn(path)

                //ACT
                tokenFilter.doFilter(request, response, filterChain)

                //ASSERT
                verify(securityContext, never())
                    .setPrincipal(LONG_TYPE.any(), listOf<GrantedAuthority>().any())

                verify(filterChain)
                    .doFilter(request, response)

                reset(filterChain)
            }
        }

    @Test
    fun `invalid - Authorization header is missing`() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/somethingElse")

        //ACT
        tokenFilter.doFilter(request, response, filterChain)

        //ASSERT
        verify(response)
            .sendError(HttpServletResponse.SC_UNAUTHORIZED.eq(), STRING_TYPE.any())
    }

    @Test
    fun `invalid - account not found`() {
        //ARRANGE
        val token = "/somethingElse"

        given(request.servletPath)
            .willReturn("/somethingElse")

        given(request.getHeader("Authorization"))
            .willReturn("Bearer $token")

        given(tokenService.getClaim(token, "id"))
            .willReturn(claim)

        given(accountService.getById(0L))
            .willThrow(AccountNotFoundException::class.java)

        //ACT
        tokenFilter.doFilter(request, response, filterChain)

        //ASSERT
        verify(response)
            .sendError(HttpServletResponse.SC_UNAUTHORIZED.eq(), STRING_TYPE.any())
    }

}