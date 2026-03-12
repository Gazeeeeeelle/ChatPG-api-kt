package com.chatpg.security.filters

import com.auth0.jwt.interfaces.Claim
import com.chatpg.domain.account.Account
import com.chatpg.exception.account.AccountNotFoundException
import com.chatpg.exception.http.sc4xx.UnauthorizedException
import com.chatpg.security.helper.SecurityContextHelper
import com.chatpg.security.token.TokenService
import com.chatpg.service.account.AccountService
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
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class TokenFilterTest: FilterTest() {

    @InjectMocks
    private lateinit var filter: TokenFilter

    @Mock private lateinit var tokenService: TokenService
    @Mock private lateinit var accountService: AccountService
    @Mock private lateinit var securityContext: SecurityContextHelper

    //Mockito stumbles into problems with Coverage because of JVM not allowing different bytecode changes in JDK 21+.
    private val publicIdString = "019c28e0-54d1-7032-8c6d-3aa2e94c639b"
    private val claim = object: Claim {
        override fun isNull(): Boolean = false
        override fun isMissing(): Boolean = false
        override fun asBoolean(): Boolean? = null
        override fun asInt(): Int? = null
        override fun asLong(): Long? = null
        override fun asDouble(): Double? = null
        override fun asString(): String = publicIdString
        override fun asDate(): Date? = null
        override fun <T> asArray(clazz: Class<T?>?): Array<out T?>? = null
        override fun <T> asList(clazz: Class<T?>?): List<T?>? = null
        override fun asMap(): Map<String?, Any?>? = null
        override fun <T> `as`(clazz: Class<T?>?): T? = null
    }

    @TestFactory
    fun `valid - paths are secured`(): Stream<DynamicTest> {
        //ARRANGE
        val token = "access-token-test"
        val publicId = UUID.fromString(publicIdString)

        val account = mock(Account::class.java)

        return Stream.of(
            "/auth/secure/logout", "/notAuthRelatedAndCheckedByTokenFilter"
        ).map { path ->
            DynamicTest.dynamicTest("path: $path") {
                //ARRANGE
                given(request.servletPath)
                    .willReturn(path)

                given(tokenService.getAccessToken(request))
                    .willReturn(token)

                given(tokenService.getClaim(token, "id"))
                    .willReturn(claim)

                given(accountService.getByPublicId(publicId))
                    .willReturn(account)

                //ACT
                filter.doFilter(request, response, filterChain)

                //ASSERT
                verify(tokenService).apply {
                    getAccessToken(request)
                    getClaim(token, "id")
                }

                verify(accountService).getByPublicId(publicId)
                verify(securityContext).setPrincipal(0L, account.authorities)

                reset(request, tokenService, accountService, securityContext)
            }
        }
    }

    @TestFactory
    fun `valid - paths are not filtered`(): Stream<DynamicTest> =
        Stream.of(
            "/auth/login", "/auth/refreshToken"
        ).map { path ->
            DynamicTest.dynamicTest("path: $path") {
                //ARRANGE
                given(request.servletPath)
                    .willReturn(path)

                //ACT
                filter.doFilter(request, response, filterChain)

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

        given(tokenService.getAccessToken(request))
            .willThrow(UnauthorizedException::class.java)

        //ACT
        filter.doFilter(request, response, filterChain)

        //ASSERT
        verify(response)
            .sendError(HttpServletResponse.SC_UNAUTHORIZED.eq(), STRING_TYPE.any())
    }

    @Test
    fun `invalid - account not found`() {
        //ARRANGE
        val token = "tokenTest"
        val publicId = UUID.fromString(publicIdString)

        given(request.servletPath)
            .willReturn("/somethingElse")

        given(tokenService.getAccessToken(request))
            .willReturn(token)

        given(tokenService.getClaim(token, "id"))
            .willReturn(claim)

        given(accountService.getByPublicId(publicId))
            .willThrow(AccountNotFoundException::class.java)

        //ACT
        filter.doFilter(request, response, filterChain)

        //ASSERT
        verify(response)
            .sendError(HttpServletResponse.SC_UNAUTHORIZED.eq(), STRING_TYPE.any())
    }

    @Test
    fun `invalid - absent claim`() {
        //ARRANGE
        val token = "tokenTest"

        given(request.servletPath)
            .willReturn("/somethingElse")

        given(tokenService.getAccessToken(request))
            .willReturn(token)

        //ACT
        filter.doFilter(request, response, filterChain)

        //ASSERT
        verify(response)
            .sendError(HttpServletResponse.SC_UNAUTHORIZED.eq(), STRING_TYPE.any())

    }

}