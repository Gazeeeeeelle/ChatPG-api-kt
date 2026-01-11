package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.exception.chat.UnauthorizedAccessToChatException
import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class AccessToChatFilterTest: FilterTest() {

    @Spy
    @InjectMocks
    lateinit var accessToChatFilter: AccessToChatFilter

    @Mock
    lateinit var validator: AccountHasAccessToChatValidator

    @Mock
    lateinit var securityContext: SecurityContext

    @Mock
    lateinit var authentication: Authentication

    @Mock
    lateinit var mockedSch: MockedStatic<SecurityContextHolder>

    @Test
    fun valid() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/chat/0/**")

        mockedSch.`when`<SecurityContext> { SecurityContextHolder.getContext() }
            .thenReturn(securityContext)

        given(securityContext.authentication)
            .willReturn(authentication)

        given(authentication.principal)
            .willReturn(0L)

        //ACT
        accessToChatFilter.doFilter(request, response, filterChain)

        //ASSERT
        Mockito.verify(filterChain)
            .doFilter(request, response)

        mockedSch.close()
    }

    @Test
    fun valid_ignored_1() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/chats/**")

        //ACT
        accessToChatFilter.doFilter(request, response, filterChain)

        //ASSERT
        Mockito.verify(filterChain)
            .doFilter(request, response)
    }

    @Test
    fun valid_ignored_2() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/account/current")

        //ACT
        accessToChatFilter.doFilter(request, response, filterChain)

        //ASSERT
        Mockito.verify(filterChain)
            .doFilter(request, response)
    }

    @Test
    fun invalid_accessDenied() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/chat/0/**")

        mockedSch.`when`<SecurityContext> { SecurityContextHolder.getContext() }
            .thenReturn(securityContext)

        given(securityContext.authentication)
            .willReturn(authentication)

        given(authentication.principal)
            .willReturn(0L)

        given(validator.validate(0L to 0L))
            .willThrow(UnauthorizedAccessToChatException::class.java)

        //ACT
        accessToChatFilter.doFilter(request, response, filterChain)

        //ASSERT
        Mockito.verify(response)
            .sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), any())

    }

}