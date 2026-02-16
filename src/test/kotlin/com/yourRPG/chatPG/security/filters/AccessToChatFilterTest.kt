package com.yourRPG.chatPG.security.filters

import com.yourRPG.chatPG.exception.chat.ForbiddenAccessToChatException
import com.yourRPG.chatPG.security.helper.SecurityContextHelper
import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.util.UUID
import java.util.stream.Stream

class AccessToChatFilterTest: FilterTest() {

    @InjectMocks
    lateinit var accessToChatFilter: AccessToChatFilter

    @Mock lateinit var validator: AccountHasAccessToChatValidator
    @Mock lateinit var securityContext: SecurityContextHelper

    @Test
    fun `valid - path is filtered and attends to the requirements`() {
        //ARRANGE
        val uuid = UUID.randomUUID()

        given(request.servletPath)
            .willReturn("/chats/by-public-id/$uuid/**")

        given(securityContext.getPrincipal())
            .willReturn(0L)

        //ACT
        accessToChatFilter.doFilterInternal(request, response, filterChain)

        //ASSERT
        verify(validator)
            .validate(0L to uuid)

        verify(filterChain)
            .doFilter(request, response)

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
                accessToChatFilter.doFilterInternal(request, response, filterChain)

                //ASSERT
                verify(filterChain)
                    .doFilter(request, response)

                Mockito.reset(filterChain)
            }
        }

    @Test
    fun `invalid - did not attend to the validation`() {
        //ARRANGE
        val uuid = UUID.randomUUID()

        given(request.servletPath)
            .willReturn("/chats/by-public-id/$uuid/**")

        given(securityContext.getPrincipal())
            .willReturn(0L)

        given(validator.validate(0L to uuid))
            .willThrow(ForbiddenAccessToChatException::class.java)

        //ACT
        accessToChatFilter.doFilterInternal(request, response, filterChain)

        //ASSERT
        verify(response)
            .sendError(eq(HttpServletResponse.SC_FORBIDDEN), any())

    }

}