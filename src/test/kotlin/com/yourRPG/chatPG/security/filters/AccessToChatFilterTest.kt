package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.exception.chat.UnauthorizedAccessToChatException
import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.stream.Stream

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
    fun `valid - path is filtered and attends to the requirements`() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/chats/0/**")

        mockedSch.`when`<SecurityContext> { SecurityContextHolder.getContext() }
            .thenReturn(securityContext)

        given(securityContext.authentication)
            .willReturn(authentication)

        given(authentication.principal)
            .willReturn(0L)

        mockedSch.`when`<SecurityContext> { SecurityContextHolder.getContext() }
            .thenReturn(securityContext)

        //ACT
        accessToChatFilter.doFilter(request, response, filterChain)

        //ASSERT
        Mockito.verify(filterChain)
            .doFilter(request, response)

        mockedSch.close()
    }

    @TestFactory
    fun `valid - paths are not filtered`(): Stream<DynamicTest> =
        Stream.of(
            "/chats/all", "/account/current"
        ).map { path ->
            DynamicTest.dynamicTest("path: $path") {
                //ARRANGE
                given(request.servletPath)
                    .willReturn(path)

                //ACT
                accessToChatFilter.doFilter(request, response, filterChain)

                //ASSERT
                Mockito.verify(filterChain)
                    .doFilter(request, response)

                Mockito.reset(filterChain)
            }
        }

    @Test
    fun `invalid - did not attend to the validation`() {
        //ARRANGE
        given(request.servletPath)
            .willReturn("/chats/0/**")

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
            .sendError(eq(HttpServletResponse.SC_FORBIDDEN), any())

    }

}